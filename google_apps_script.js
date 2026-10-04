/**
 * BEAUTYPASS — GOOGLE APPS SCRIPT BACKEND
 * Endpoint REST para coleta de eventos, reservas e questionários SUS.
 * 
 * Instruções de Implantação:
 * 1. Abra a planilha do Google Sheets criada para o teste de validação.
 * 2. Acesse Extensões > Apps Script.
 * 3. Cole este código no arquivo Código.gs.
 * 4. Execute a função setupSpreadsheetSheets() uma vez para criar as abas e cabeçalhos.
 * 5. Clique em Implantar > Nova Implantação > Tipo: App da Web.
 * 6. Executar como: 'Eu' (sua conta) | Quem pode acessar: 'Qualquer pessoa'.
 * 7. Copie a URL gerada e configure em app.js (CONFIG.APPS_SCRIPT_URL).
 */

function setupSpreadsheetSheets() {
  const ss = SpreadsheetApp.getActiveSpreadsheet();
  
  // Aba 1: Eventos
  let sheetEvt = ss.getSheetByName('Eventos') || ss.insertSheet('Eventos');
  if (sheetEvt.getLastRow() === 0) {
    sheetEvt.appendRow(['id', 'server_ts', 'client_ts', 'session_id', 'participant_code', 'event_name', 'props_json']);
    sheetEvt.getRange(1, 1, 1, 7).setFontWeight('bold').setBackground('#E6FFFA');
  }

  // Aba 2: Reservas (Slots Ativos com expiração)
  let sheetRes = ss.getSheetByName('Reservas') || ss.insertSheet('Reservas');
  if (sheetRes.getLastRow() === 0) {
    sheetRes.appendRow(['booking_id', 'created_at', 'expires_at', 'salon_id', 'date', 'time', 'staff_id', 'participant_code', 'status']);
    sheetRes.getRange(1, 1, 1, 9).setFontWeight('bold').setBackground('#FEF3C7');
  }

  // Aba 3: SUS_Avaliacoes
  let sheetSUS = ss.getSheetByName('SUS_Avaliacoes') || ss.insertSheet('SUS_Avaliacoes');
  if (sheetSUS.getLastRow() === 0) {
    sheetSUS.appendRow([
      'eval_id', 'timestamp', 'participant_code', 'session_id', 
      'q1', 'q2', 'q3', 'q4', 'q5', 'q6', 'q7', 'q8', 'q9', 'q10', 
      'sus_score', 'liked_feedback', 'disliked_feedback', 'would_use_again'
    ]);
    sheetSUS.getRange(1, 1, 1, 18).setFontWeight('bold').setBackground('#E0E7FF');
  }

  // Aba 4: Moderador (Controle de Tarefas e Tempos)
  let sheetMod = ss.getSheetByName('Moderador') || ss.insertSheet('Moderador');
  if (sheetMod.getLastRow() === 0) {
    sheetMod.appendRow([
      'participant_code', 'persona', 'date', 't1_success', 't1_time_sec', 
      't2_success', 't2_time_sec', 't3_success', 't3_time_sec', 
      't4_success', 't4_time_sec', 'instagram_time_sec', 'h4_faster', 'notes'
    ]);
    sheetMod.getRange(1, 1, 1, 14).setFontWeight('bold').setBackground('#FCE7F3');
  }

  // Aba 5: Relatorio_Metricas (Fórmulas Automáticas)
  let sheetRel = ss.getSheetByName('Relatorio_Metricas') || ss.insertSheet('Relatorio_Metricas');
  if (sheetRel.getLastRow() === 0) {
    sheetRel.appendRow(['Métrica de Validação', 'Meta GO', 'Fórmula / Valor Atual', 'Status']);
    sheetRel.appendRow(['H1: % Agendamentos com Desconto', '>= 35%', '=IFERROR(COUNTIFS(Eventos!F:F, "checkout_completed", Eventos!G:G, "*\"discount_applied\":[1-9]*") / COUNTIF(Eventos!F:F, "checkout_completed"), 0)', 'Verificar']);
    sheetRel.appendRow(['H2: Conclusão Checkout', '>= 50%', '=IFERROR(COUNTIF(Eventos!F:F, "checkout_completed") / COUNTIF(Eventos!F:F, "checkout_started"), 0)', 'Verificar']);
    sheetRel.appendRow(['SUS Global Médio', '>= 70 pts', '=IFERROR(AVERAGE(SUS_Avaliacoes!O:O), 0)', 'Verificar']);
    sheetRel.appendRow(['Intenção de Retenção', '>= 60%', '=IFERROR(COUNTIF(SUS_Avaliacoes!R:R, TRUE) / COUNT(SUS_Avaliacoes!O:O), 0)', 'Verificar']);
    sheetRel.getRange(1, 1, 1, 4).setFontWeight('bold').setBackground('#DCFCE7');
  }
}

function doGet(e) {
  return ContentService.createTextOutput(JSON.stringify({
    status: 'ONLINE',
    service: 'BeautyPass Data Collection API',
    timestamp: new Date().toISOString()
  })).setMimeType(ContentService.MimeType.JSON);
}

function doPost(e) {
  try {
    if (!e || !e.postData || !e.postData.contents) {
      return ContentService.createTextOutput(JSON.stringify({ status: 'error', message: 'No payload received' }))
        .setMimeType(ContentService.MimeType.JSON);
    }

    const data = JSON.parse(e.postData.contents);
    const ss = SpreadsheetApp.getActiveSpreadsheet();

    // Rota 1: Ingestão de Lote de Eventos Analíticos
    if (data.action === 'log_events' && Array.isArray(data.events)) {
      const sheet = ss.getSheetByName('Eventos');
      const now = new Date().toISOString();
      const rows = data.events.map(ev => [
        ev.id || ('evt_' + Math.random().toString(36).substr(2, 7)),
        now,
        ev.clientTs || now,
        ev.sessionId || 'sess_unknown',
        ev.props?.participant_code || 'P00',
        ev.eventName,
        JSON.stringify(ev.props || {})
      ]);
      if (rows.length > 0) {
        sheet.getRange(sheet.getLastRow() + 1, 1, rows.length, rows[0].length).setValues(rows);
      }
      return ContentService.createTextOutput(JSON.stringify({ status: 'ok', count: rows.length }))
        .setMimeType(ContentService.MimeType.JSON);
    }

    // Rota 2: Tentativa de Reserva com Checagem de Conflito (60 min)
    if (data.action === 'attempt_booking') {
      const sheet = ss.getSheetByName('Reservas');
      const now = new Date();
      const salonId = String(data.salon_id);
      const dateStr = String(data.date);
      const timeStr = String(data.time);
      const staffId = String(data.staff_id || 'any');

      // Limpa ou ignora reservas com mais de 60 minutos
      const values = sheet.getDataRange().getValues();
      let hasConflict = false;

      for (let i = 1; i < values.length; i++) {
        const row = values[i];
        const rExpires = new Date(row[2]);
        const rSalon = String(row[3]);
        const rDate = String(row[4]);
        const rTime = String(row[5]);
        const rStaff = String(row[6]);
        const rStatus = String(row[8]);

        // Se ainda não expirou e é o mesmo salão, data e horário
        if (rExpires > now && rStatus === 'ACTIVE' && rSalon === salonId && rDate === dateStr && rTime === timeStr) {
          // Se for o mesmo profissional ou um dos dois for 'any'
          if (rStaff === staffId || staffId === 'any' || rStaff === 'any') {
            hasConflict = true;
            break;
          }
        }
      }

      if (hasConflict) {
        return ContentService.createTextOutput(JSON.stringify({ 
          status: 'CONFLICT', 
          message: 'Outro participante acabou de reservar este horário.' 
        })).setMimeType(ContentService.MimeType.JSON);
      }

      // Reserva liberada: registrar com validade de 60 minutos
      const expiresAt = new Date(now.getTime() + 60 * 60 * 1000).toISOString();
      sheet.appendRow([
        data.booking_id || ('BP-' + Math.floor(100000 + Math.random() * 900000)),
        now.toISOString(),
        expiresAt,
        salonId,
        dateStr,
        timeStr,
        staffId,
        data.participant_code || 'P00',
        'ACTIVE'
      ]);

      return ContentService.createTextOutput(JSON.stringify({ status: 'RESERVED', expiresAt }))
        .setMimeType(ContentService.MimeType.JSON);
    }

    // Rota 3: Envio de Questionário SUS
    if (data.action === 'submit_sus') {
      const sheet = ss.getSheetByName('SUS_Avaliacoes');
      const now = new Date().toISOString();
      const q = data.susAnswers || [];
      sheet.appendRow([
        data.id || ('eval_' + Math.random().toString(36).substr(2, 7)),
        now,
        data.participantCode || 'P00',
        data.sessionId || 'sess_unknown',
        q[0], q[1], q[2], q[3], q[4], q[5], q[6], q[7], q[8], q[9],
        data.susScore || 0,
        data.feedbackLiked || '',
        data.feedbackDisliked || '',
        Boolean(data.wouldUseAgain)
      ]);
      return ContentService.createTextOutput(JSON.stringify({ status: 'ok' }))
        .setMimeType(ContentService.MimeType.JSON);
    }

    return ContentService.createTextOutput(JSON.stringify({ status: 'unknown_action' }))
      .setMimeType(ContentService.MimeType.JSON);

  } catch (error) {
    return ContentService.createTextOutput(JSON.stringify({ status: 'error', error: error.toString() }))
      .setMimeType(ContentService.MimeType.JSON);
  }
}
