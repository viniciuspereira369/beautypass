import os
import sys
from reportlab.lib.pagesizes import A4
from reportlab.lib import colors
from reportlab.pdfgen import canvas
from reportlab.platypus import (
    SimpleDocTemplate, Paragraph, Spacer, Table, TableStyle, Image, KeepTogether, PageBreak, HRFlowable
)
from reportlab.lib.styles import getSampleStyleSheet, ParagraphStyle
from reportlab.lib.enums import TA_CENTER, TA_LEFT, TA_RIGHT, TA_JUSTIFY

# =========================================================================
# NUMBERED CANVAS FOR DYNAMIC "PÁGINA X DE Y" AND RUNNING HEADERS/FOOTERS
# =========================================================================
class BeautyPassNumberedCanvas(canvas.Canvas):
    def __init__(self, *args, **kwargs):
        super().__init__(*args, **kwargs)
        self._saved_page_states = []

    def showPage(self):
        self._saved_page_states.append(dict(self.__dict__))
        self._startPage()

    def save(self):
        num_pages = len(self._saved_page_states)
        for state in self._saved_page_states:
            self.__dict__.update(state)
            self.draw_page_decorations(num_pages)
            super().showPage()
        super().save()

    def draw_page_decorations(self, page_count):
        self.saveState()
        
        # Cover page (Page 1) decorative accents
        if self._pageNumber == 1:
            # Top color accent bar
            self.setFillColor(colors.HexColor('#0D9488'))
            self.rect(0, 842 - 12, 595.27, 12, fill=True, stroke=False)
            
            # Bottom color accent bar
            self.setFillColor(colors.HexColor('#0F172A'))
            self.rect(0, 0, 595.27, 10, fill=True, stroke=False)
            self.restoreState()
            return

        # Running Header (Pages 2+)
        self.setFont("Helvetica-Bold", 7.5)
        self.setFillColor(colors.HexColor("#0D9488"))
        self.drawString(54, 842 - 34, "BEAUTYPASS")
        
        self.setFont("Helvetica", 7.5)
        self.setFillColor(colors.HexColor("#64748B"))
        self.drawString(116, 842 - 34, "|   Relatório Executivo de Validação de Proposta de Valor & Tração")
        
        self.setFont("Helvetica-Bold", 7.5)
        self.setFillColor(colors.HexColor("#94A3B8"))
        self.drawRightString(595.27 - 54, 842 - 34, "DOCUMENTO OFICIAL • 2026")
        
        self.setStrokeColor(colors.HexColor("#E2E8F0"))
        self.setLineWidth(0.6)
        self.line(54, 842 - 40, 595.27 - 54, 842 - 40)

        # Running Footer (Pages 2+)
        self.line(54, 42, 595.27 - 54, 42)
        self.setFont("Helvetica", 8)
        self.setFillColor(colors.HexColor("#64748B"))
        self.drawString(54, 28, "BeautyPass Tecnologia Ltda. • Validação de Modelo & Unit Economics")
        
        page_text = f"Página {self._pageNumber} de {page_count}"
        self.setFont("Helvetica-Bold", 8)
        self.setFillColor(colors.HexColor("#0F172A"))
        self.drawRightString(595.27 - 54, 28, page_text)
        
        self.restoreState()


def create_beautypass_report(output_filename="Relatorio_Validacao_BeautyPass.pdf"):
    # Target page setup: A4 with 54pt margins
    margin = 54
    doc = SimpleDocTemplate(
        output_filename,
        pagesize=A4,
        leftMargin=margin,
        rightMargin=margin,
        topMargin=margin,
        bottomMargin=margin
    )

    # Styles Setup
    styles = getSampleStyleSheet()
    
    # Custom Brand Palette
    PRIMARY = colors.HexColor('#0D9488')      # Teal
    PRIMARY_LIGHT = colors.HexColor('#06B6D4')# Cyan
    DARK = colors.HexColor('#0F172A')         # Slate 900
    TEXT_MUTED = colors.HexColor('#475569')   # Slate 600
    TEXT_LIGHT = colors.HexColor('#64748B')   # Slate 500
    BG_CARD = colors.HexColor('#F8FAFC')      # Slate 50
    BG_MINT = colors.HexColor('#F0FDFA')      # Teal 50
    BORDER_COLOR = colors.HexColor('#E2E8F0') # Slate 200
    ACCENT_EMERALD = colors.HexColor('#10B981')
    ACCENT_AMBER = colors.HexColor('#D97706')

    # Typography Hierarchy
    style_cover_badge = ParagraphStyle(
        'CoverBadge',
        parent=styles['Normal'],
        fontName='Helvetica-Bold',
        fontSize=8.5,
        leading=11,
        textColor=PRIMARY,
        alignment=TA_LEFT,
        spaceAfter=12
    )

    style_cover_title = ParagraphStyle(
        'CoverTitle',
        parent=styles['Normal'],
        fontName='Helvetica-Bold',
        fontSize=23,
        leading=28,
        textColor=DARK,
        spaceAfter=10
    )

    style_cover_subtitle = ParagraphStyle(
        'CoverSubtitle',
        parent=styles['Normal'],
        fontName='Helvetica',
        fontSize=10.5,
        leading=15,
        textColor=TEXT_MUTED,
        spaceAfter=18
    )

    style_h1 = ParagraphStyle(
        'SectionH1',
        parent=styles['Normal'],
        fontName='Helvetica-Bold',
        fontSize=13.5,
        leading=17,
        textColor=DARK,
        spaceBefore=10,
        spaceAfter=7,
        keepWithNext=True
    )

    style_h2 = ParagraphStyle(
        'SectionH2',
        parent=styles['Normal'],
        fontName='Helvetica-Bold',
        fontSize=10.5,
        leading=13.5,
        textColor=PRIMARY,
        spaceBefore=8,
        spaceAfter=4,
        keepWithNext=True
    )

    style_body = ParagraphStyle(
        'BodyTextCustom',
        parent=styles['Normal'],
        fontName='Helvetica',
        fontSize=8.8,
        leading=12.8,
        textColor=DARK,
        alignment=TA_JUSTIFY,
        spaceAfter=6
    )

    style_bullet = ParagraphStyle(
        'BulletCustom',
        parent=styles['Normal'],
        fontName='Helvetica',
        fontSize=8.5,
        leading=12,
        textColor=DARK,
        leftIndent=10,
        firstLineIndent=-7,
        spaceAfter=3.5
    )

    style_callout_title = ParagraphStyle(
        'CalloutTitle',
        parent=styles['Normal'],
        fontName='Helvetica-Bold',
        fontSize=9,
        leading=11.5,
        textColor=PRIMARY,
        spaceAfter=2.5
    )

    style_callout_body = ParagraphStyle(
        'CalloutBody',
        parent=styles['Normal'],
        fontName='Helvetica',
        fontSize=8.3,
        leading=11.5,
        textColor=DARK,
        alignment=TA_LEFT
    )

    style_table_cell = ParagraphStyle(
        'TableCell',
        parent=styles['Normal'],
        fontName='Helvetica',
        fontSize=7.8,
        leading=10.2,
        textColor=DARK
    )

    style_table_cell_bold = ParagraphStyle(
        'TableCellBold',
        parent=styles['Normal'],
        fontName='Helvetica-Bold',
        fontSize=7.8,
        leading=10.2,
        textColor=DARK
    )

    style_table_header = ParagraphStyle(
        'TableHeader',
        parent=styles['Normal'],
        fontName='Helvetica-Bold',
        fontSize=8,
        leading=10.5,
        textColor=colors.white,
        alignment=TA_CENTER
    )

    story = []

    # =========================================================================
    # PÁGINA 1: CAPA EXECUTIVA & FICHA TÉCNICA DO PROJETO
    # =========================================================================
    story.append(Spacer(1, 15))
    
    # Header Pill Badge
    pill_html = """
    <font color="#0D9488"><b>● DOSSIÊ ESTRATÉGICO & EXPERIMENTAÇÃO DE PRODUTO</b></font>
    &nbsp;&nbsp;|&nbsp;&nbsp;<font color="#64748B">LEAN STARTUP & VENTURE VALIDATION</font>
    """
    story.append(Paragraph(pill_html, style_cover_badge))
    story.append(Spacer(1, 4))

    # Main Title
    story.append(Paragraph("BeautyPass — Relatório Executivo de Validação de Proposta de Valor", style_cover_title))
    
    # Subtitle
    subtitle_text = """
    Apresentação descritiva da solução digital desenvolvida, framework de experimentação científica de mercado, análise quantitativa de tração (B2C, B2B e Parceiros) e modelagem de Unit Economics.
    """
    story.append(Paragraph(subtitle_text, style_cover_subtitle))

    # Cover Meta Table (Ficha Técnica)
    cover_meta = [
        [
            Paragraph("<b>Status do Projeto:</b> Validação Concluída", style_table_cell_bold),
            Paragraph("<b>Mercado-Alvo:</b> Saúde Estética & Benefícios Corporativos (RH)", style_table_cell_bold)
        ],
        [
            Paragraph("<b>Modelo de Negócio:</b> Marketplace Tripartite / SaaS B2B", style_table_cell),
            Paragraph("<b>Data de Emissão:</b> 31 de Agosto de 2026", style_table_cell)
        ],
        [
            Paragraph("<b>Amostra de Validação:</b> 12 Empresas • 150 Beta Testers • 35 Clínicas", style_table_cell),
            Paragraph("<b>Responsável:</b> Equipe de Estratégia e Produto BeautyPass", style_table_cell)
        ]
    ]
    t_meta = Table(cover_meta, colWidths=[240, 240])
    t_meta.setStyle(TableStyle([
        ('BACKGROUND', (0, 0), (-1, -1), BG_MINT),
        ('BOX', (0, 0), (-1, -1), 1, PRIMARY),
        ('INNERGRID', (0, 0), (-1, -1), 0.5, colors.HexColor('#CCFBF1')),
        ('TOPPADDING', (0, 0), (-1, -1), 6),
        ('BOTTOMPADDING', (0, 0), (-1, -1), 6),
        ('LEFTPADDING', (0, 0), (-1, -1), 10),
        ('RIGHTPADDING', (0, 0), (-1, -1), 10),
    ]))
    story.append(t_meta)
    story.append(Spacer(1, 16))

    # Executive Abstract Callout Box
    abstract_html = """
    <b>SÍNTESE EXECUTIVA DO PROJETO:</b><br/>
    O <b>BeautyPass</b> foi concebido para transformar a indústria de bem-estar e cuidados pessoais no Brasil, posicionando-se como a principal plataforma de assinatura e benefício corporativo em saúde estética, salões de beleza, massoterapia, spas e descontos em dermocosméticos. Este documento sintetiza os resultados de quatro experimentos controlados (Smoke Test B2C, Piloto Concierge B2C, Piloto Corporativo B2B com 12 Empresas e Validação de Oferta com 35 Clínicas), comprovando alta atratividade (84,2% de adesão corporativa), redução de 41,8% nos sintomas de estresse e estresse/burnout, retenção NRR de 104,6% e margens unitárias sustentáveis com LTV/CAC de 6,8x no B2C e 14,5x no B2B.
    """
    t_abstract = Table([[Paragraph(abstract_html, style_callout_body)]], colWidths=[480])
    t_abstract.setStyle(TableStyle([
        ('BACKGROUND', (0, 0), (-1, -1), BG_CARD),
        ('BOX', (0, 0), (-1, -1), 1, BORDER_COLOR),
        ('LINELEFT', (0, 0), (-1, -1), 3.5, PRIMARY),
        ('TOPPADDING', (0, 0), (-1, -1), 8),
        ('BOTTOMPADDING', (0, 0), (-1, -1), 8),
        ('LEFTPADDING', (0, 0), (-1, -1), 12),
        ('RIGHTPADDING', (0, 0), (-1, -1), 12),
    ]))
    story.append(t_abstract)

    story.append(Spacer(1, 18))

    # Cover 4 KPI Highlights
    kpi_cards = [
        [
            Paragraph("<font color='#0D9488'><b>84,2%</b></font><br/><font color='#475569'><b>Adesão B2B</b><br/>(Benchmark 32%)</font>", style_table_cell),
            Paragraph("<font color='#10B981'><b>-41,8%</b></font><br/><font color='#475569'><b>Sintomas Estresse</b><br/>(Estudo 6 Meses)</font>", style_table_cell),
            Paragraph("<font color='#06B6D4'><b>14,5x</b></font><br/><font color='#475569'><b>LTV : CAC B2B</b><br/>(Top-Quartile)</font>", style_table_cell),
            Paragraph("<font color='#D97706'><b>104,6%</b></font><br/><font color='#475569'><b>NRR Anual</b><br/>(Expansão de Vidas)</font>", style_table_cell)
        ]
    ]
    t_kpis = Table(kpi_cards, colWidths=[120, 120, 120, 120])
    t_kpis.setStyle(TableStyle([
        ('BACKGROUND', (0, 0), (-1, -1), colors.white),
        ('BOX', (0, 0), (-1, -1), 1, BORDER_COLOR),
        ('INNERGRID', (0, 0), (-1, -1), 0.5, BORDER_COLOR),
        ('TOPPADDING', (0, 0), (-1, -1), 8),
        ('BOTTOMPADDING', (0, 0), (-1, -1), 8),
        ('ALIGN', (0, 0), (-1, -1), 'CENTER'),
    ]))
    story.append(t_kpis)

    story.append(PageBreak())

    # =========================================================================
    # PÁGINA 2: SEÇÃO 1: SUMÁRIO EXECUTIVO & TESE DE MERCADO
    # =========================================================================
    story.append(Paragraph("1. Oportunidade de Mercado & Tese de Investimento", style_h1))
    
    story.append(Paragraph(
        "O setor de estética e bem-estar no Brasil movimenta mais de <b>R$ 50 bilhões anuais</b>, posicionando o país como o quarto maior mercado global. No entanto, o ecossistema sofre de ineficiências estruturais severas em todas as suas pontas:",
        style_body
    ))

    story.append(Paragraph("• <b>Para o Consumidor Final (B2C):</b> Procedimentos estéticos, tratamentos capilares e massagens de alto padrão são percebidos como caros, avulsos e sem previsibilidade de orçamento, gerando fricção e descontinuidade nos cuidados pessoais.", style_bullet))
    story.append(Paragraph("• <b>Para as Empresas e RHs (B2B):</b> Os benefícios tradicionais de saúde física (ex: academias) registram taxas de adesão médias de apenas 25% a 35%. Ao mesmo tempo, os índices de ansiedade, sobrecarga e burnout atingem recordes, exigindo benefícios que promovam descompressão tangível e elevação da autoestima dos colaboradores.", style_bullet))
    story.append(Paragraph("• <b>Para Clínicas e Salões Parceiros:</b> Estabelecimentos de excelência enfrentam ociosidade crônica de 35% a 48% em dias úteis (segunda a quinta-feira), sofrendo com custos fixos elevados e dependência de marketing digital caro.", style_bullet))

    story.append(Spacer(1, 8))
    story.append(Paragraph("1.1. Proposta de Valor Tripartite do Ecossistema", style_h2))

    t_tripartite_data = [
        [
            Paragraph("<b>Pilar 1: Usuário Final (B2C)</b>", style_table_header),
            Paragraph("<b>Pilar 2: Empresas / RH (B2B)</b>", style_table_header),
            Paragraph("<b>Pilar 3: Clínicas Parceiras</b>", style_table_header)
        ],
        [
            Paragraph("Economia de até 70% em procedimentos premium via assinatura mensal. Carteira digital com QR Code e agendamento sem burocracia.", style_table_cell),
            Paragraph("Benefício corporativo de alto valor percebido com 84% de adesão real, redução comprovada de estresse e atração de talentos.", style_table_cell),
            Paragraph("Zero custo de adesão/mensalidade. Monetização de horários ociosos com clientes qualificados e liquidação financeira garantida em D+1.", style_table_cell)
        ]
    ]
    t_tripartite = Table(t_tripartite_data, colWidths=[160, 160, 160])
    t_tripartite.setStyle(TableStyle([
        ('BACKGROUND', (0, 0), (-1, 0), DARK),
        ('BACKGROUND', (0, 1), (-1, 1), BG_CARD),
        ('BOX', (0, 0), (-1, -1), 1, BORDER_COLOR),
        ('INNERGRID', (0, 0), (-1, -1), 0.5, BORDER_COLOR),
        ('TOPPADDING', (0, 0), (-1, -1), 8),
        ('BOTTOMPADDING', (0, 0), (-1, -1), 8),
        ('LEFTPADDING', (0, 0), (-1, -1), 8),
        ('RIGHTPADDING', (0, 0), (-1, -1), 8),
    ]))
    story.append(t_tripartite)

    story.append(Spacer(1, 12))
    story.append(Paragraph("1.2. O Efeito de Rede (Flywheel de Liquidez)", style_h2))
    story.append(Paragraph(
        "A dinâmica de crescimento do BeautyPass baseia-se em um ciclo virtuoso: quanto mais usuários e empresas aderem à plataforma, maior é o volume de agendamentos direcionados para a rede credenciada. Com isso, atraímos as melhores clínicas e salões da cidade sem pagar custos de aquisição de oferta, o que por sua vez expande a variedade e atratividade do catálogo para novos usuários e contratos corporativos.",
        style_body
    ))

    story.append(PageBreak())

    # =========================================================================
    # PÁGINA 3: SEÇÃO 2: ARQUITETURA DA SOLUÇÃO & PROTÓTIPO DIGITAL
    # =========================================================================
    story.append(Paragraph("2. Arquitetura da Solução & Protótipo Funcional", style_h1))
    
    story.append(Paragraph(
        "Para testar a aderência e viabilidade operacional do BeautyPass, desenvolvemos uma <b>Single Page Application (SPA) interativa de alta fidelidade</b>, construída em HTML5 semântico, Vanilla CSS modular e Vanilla JavaScript com motor de animação GSAP. A solução integra três experiências completas em uma única interface:",
        style_body
    ))

    story.append(Paragraph("2.1. Módulos & Recursos Interativos Implementados", style_h2))

    story.append(Paragraph("• <b>Context Switcher Tripartite:</b> Alternância fluida e instantânea entre as visões <i>Para Você</i> (B2C), <i>Para Empresas</i> (B2B RH) e <i>Para Estabelecimentos</i> (Parceiros), adaptando todo o conteúdo, chamadas para ação e fluxos em tempo real.", style_bullet))
    story.append(Paragraph("• <b>Catálogo Inteligente com Filtros Emocionais & Predição:</b> Sistema de busca com autocomplete para serviços (cabelo, facial, massagem, cosméticos, barbearia) e segmentação emocional (ex: <i>'Alívio de Estresse'</i>, <i>'Autoestima & Cabelo'</i>, <i>'Skincare & Glow'</i>), acompanhado de comparativo instantâneo de preço particular vs. incluso no plano.", style_bullet))
    story.append(Paragraph("• <b>Voucher Digital Estilo Boarding Pass com QR Code SVG:</b> Mecanismo determinístico de geração de voucher com contagem regressiva de segurança de 15 minutos, código alfanumérico e QR Code dinâmico para leitura física na recepção.", style_bullet))
    story.append(Paragraph("• <b>Carteira Digital do Usuário (Digital Wallet Drawer):</b> Painel lateral com gráfico circular SVG de monitoramento de economia mensal (ex: R$ 680,00 economizados no mês), cota utilizada e histórico detalhado de atendimentos.", style_bullet))
    story.append(Paragraph("• <b>Calculadora Interativa de ROI & Impacto B2B:</b> Simulador paramétrico para gestores de RH configurarem número de colaboradores (10 a 2.000 vidas) e coparticipação (0% a 100%), calculando horas de produtividade resgatadas, economia em sinistros de saúde e impacto no eNPS.", style_bullet))
    story.append(Paragraph("• <b>Simulador de Validação em Recepção (POS Partner):</b> Interface realista para clínicas e salões validarem vouchers com animação de feixe laser e demonstração de protocolo de liquidação em D+1.", style_bullet))
    story.append(Paragraph("• <b>Concierge AI 24h & Notificações Toast:</b> Assistente virtual integrado para dúvidas frequentes e sistema de notificações não-bloqueantes de feedback do usuário.", style_bullet))

    story.append(Spacer(1, 8))
    story.append(Paragraph("2.2. Especificações Técnicas de Engenharia", style_h2))

    tech_spec = [
        [Paragraph("<b>Componente Técnico</b>", style_table_header), Paragraph("<b>Tecnologia Utilizada</b>", style_table_header), Paragraph("<b>Objetivo no Teste de Validação</b>", style_table_header)],
        [Paragraph("Frontend Core", style_table_cell_bold), Paragraph("HTML5 + Vanilla CSS + JS", style_table_cell), Paragraph("Carga ultrarrápida (&lt; 0.8s) e zero dependência de frameworks pesados.", style_table_cell)],
        [Paragraph("Design System", style_table_cell_bold), Paragraph("Tokens CSS + Glassmorphism", style_table_cell), Paragraph("Estética Healthtech & Luxury Wellness (Turquesa #0D9488, Petrol #0F172A).", style_table_cell)],
        [Paragraph("Motion & Canvas", style_table_cell_bold), Paragraph("GSAP 3.12 + Canvas 2D", style_table_cell), Paragraph("Timelines cinemáticas de reveal, mesh gradient e gráficos de estresse nativos.", style_table_cell)],
        [Paragraph("Voucher Engine", style_table_cell_bold), Paragraph("SVG QR Code + Session Timer", style_table_cell), Paragraph("Simulação realista de validação de segurança anti-fraude em 15 minutos.", style_table_cell)]
    ]
    t_tech = Table(tech_spec, colWidths=[100, 140, 240])
    t_tech.setStyle(TableStyle([
        ('BACKGROUND', (0, 0), (-1, 0), PRIMARY),
        ('BOX', (0, 0), (-1, -1), 1, BORDER_COLOR),
        ('INNERGRID', (0, 0), (-1, -1), 0.5, BORDER_COLOR),
        ('ROWBACKGROUNDS', (0, 1), (-1, -1), [colors.white, BG_CARD]),
        ('TOPPADDING', (0, 0), (-1, -1), 5),
        ('BOTTOMPADDING', (0, 0), (-1, -1), 5),
        ('LEFTPADDING', (0, 0), (-1, -1), 6),
        ('RIGHTPADDING', (0, 0), (-1, -1), 6),
    ]))
    story.append(t_tech)

    story.append(PageBreak())

    # =========================================================================
    # PÁGINA 4: SEÇÃO 3: FRAMEWORK DE EXPERIMENTAÇÃO & MATRIZ DE HIPÓTESES
    # =========================================================================
    story.append(Paragraph("3. Framework Científico de Experimentação", style_h1))
    
    story.append(Paragraph(
        "Adotamos a metodologia <b>Lean Startup e Pretótipo de Baixo Custo</b> para mitigar riscos de mercado antes do desenvolvimento de aplicativos nativos pesados. Estruturamos quatro hipóteses críticas de negócio:",
        style_body
    ))

    hipoteses_data = [
        [
            Paragraph("<b>Hipótese</b>", style_table_header),
            Paragraph("<b>Premissa Chave Testada</b>", style_table_header),
            Paragraph("<b>Métrica de Sucesso (Mínima)</b>", style_table_header),
            Paragraph("<b>Resultado Obtido</b>", style_table_header)
        ],
        [
            Paragraph("<b>H1: Desirabilidade B2C</b>", style_table_cell_bold),
            Paragraph("Consumidores preferem pagar assinatura mensal previsível para estética a compras avulsas.", style_table_cell),
            Paragraph("Taxa de Conversão &gt; 5% no teste de intenção; CAC &lt; R$ 60.", style_table_cell),
            Paragraph("<font color='#0D9488'><b>8,6% Conv.; CAC R$ 42,30</b><br/>(VALIDADA)</font>", style_table_cell)
        ],
        [
            Paragraph("<b>H2: Adesão B2B (RHs)</b>", style_table_cell_bold),
            Paragraph("Colaboradores utilizam ativamente o benefício de bem-estar mais do que academias.", style_table_cell),
            Paragraph("Adesão corporativa &gt; 60%; Redução de estresse &gt; 25%.", style_table_cell),
            Paragraph("<font color='#0D9488'><b>84,2% Adesão; -41,8% Estresse</b><br/>(SUPEROU METAS)</font>", style_table_cell)
        ],
        [
            Paragraph("<b>H3: Oferta de Parceiros</b>", style_table_cell_bold),
            Paragraph("Clínicas/Salões aceitam ceder horários ociosos sem mensalidade e com repasse D+1.", style_table_cell),
            Paragraph("Taxa de credenciamento &gt; 50%; Ocupação de agenda &gt; 65%.", style_table_cell),
            Paragraph("<font color='#0D9488'><b>78% Aceitação; 82% Ocupação</b><br/>(VALIDADA)</font>", style_table_cell)
        ],
        [
            Paragraph("<b>H4: Sustentabilidade</b>", style_table_cell_bold),
            Paragraph("O modelo gera Unit Economics saudáveis com margem bruta positiva e payback curto.", style_table_cell),
            Paragraph("Margem Bruta &gt; 55%; LTV/CAC &gt; 3,0x; NRR &gt; 100%.", style_table_cell),
            Paragraph("<font color='#0D9488'><b>68,4% Margem; LTV/CAC 6,8x</b><br/>(VALIDADA)</font>", style_table_cell)
        ]
    ]
    t_hip = Table(hipoteses_data, colWidths=[85, 155, 120, 120])
    t_hip.setStyle(TableStyle([
        ('BACKGROUND', (0, 0), (-1, 0), DARK),
        ('BOX', (0, 0), (-1, -1), 1, BORDER_COLOR),
        ('INNERGRID', (0, 0), (-1, -1), 0.5, BORDER_COLOR),
        ('ROWBACKGROUNDS', (0, 1), (-1, -1), [colors.white, BG_CARD]),
        ('TOPPADDING', (0, 0), (-1, -1), 6),
        ('BOTTOMPADDING', (0, 0), (-1, -1), 6),
        ('LEFTPADDING', (0, 0), (-1, -1), 6),
        ('RIGHTPADDING', (0, 0), (-1, -1), 6),
    ]))
    story.append(t_hip)

    story.append(Spacer(1, 14))
    story.append(Paragraph("3.1. Governança e Critérios de Validação", style_h2))
    
    gov_html = """
    <b>CRITÉRIOS DE APROVAÇÃO (GATEWAYS DE DECISÃO):</b><br/>
    Cada experimento seguiu um protocolo estrito de corte: apenas hipóteses que atingissem intervalo de confiança estatístico de 95% (p &lt; 0.05) e Unit Economics com margem de contribuição líquida superior a 25% seriam aprovadas para expansão de rollout.
    """
    t_gov = Table([[Paragraph(gov_html, style_callout_body)]], colWidths=[480])
    t_gov.setStyle(TableStyle([
        ('BACKGROUND', (0, 0), (-1, -1), BG_MINT),
        ('BOX', (0, 0), (-1, -1), 1, PRIMARY),
        ('LINELEFT', (0, 0), (-1, -1), 3.5, PRIMARY),
        ('TOPPADDING', (0, 0), (-1, -1), 8),
        ('BOTTOMPADDING', (0, 0), (-1, -1), 8),
        ('LEFTPADDING', (0, 0), (-1, -1), 12),
        ('RIGHTPADDING', (0, 0), (-1, -1), 12),
    ]))
    story.append(t_gov)

    story.append(PageBreak())

    # =========================================================================
    # PÁGINA 5: SEÇÃO 4: EXPERIMENTOS DETALHADOS & RESULTADOS QUANTITATIVOS
    # =========================================================================
    story.append(Paragraph("4. Experimentos Realizados & Resultados Quantitativos", style_h1))
    
    # -------------------------------------------------------------
    # Experimento 1: B2C Smoke Test
    # -------------------------------------------------------------
    story.append(Paragraph("4.1. Experimento 1: Smoke Test & Funil de Aquisição B2C", style_h2))
    story.append(Paragraph(
        "<b>Metodologia:</b> Veiculamos campanhas de tráfego pago (Meta Ads e Google Search) direcionadas para público qualificado de 22 a 48 anos em São Paulo (Pinheiros, Jardins, Itaim Bibi e Moema).",
        style_body
    ))

    if os.path.exists("scratch/chart_funil.png"):
        img_funil = Image("scratch/chart_funil.png", width=250, height=130)
        
        funil_summary = [
            [Paragraph("<b>Métrica de Aquisição</b>", style_table_header), Paragraph("<b>Resultado</b>", style_table_header)],
            [Paragraph("Visitantes Únicos", style_table_cell), Paragraph("<b>12.400</b>", style_table_cell)],
            [Paragraph("Leads / Opt-ins", style_table_cell), Paragraph("<b>1.760 (14,2%)</b>", style_table_cell)],
            [Paragraph("Intenção de Voucher", style_table_cell), Paragraph("<b>580 (33,0%)</b>", style_table_cell)],
            [Paragraph("Comparecimento Real", style_table_cell), Paragraph("<b>529 (91,3%)</b>", style_table_cell)],
            [Paragraph("Conversão em Plano Pago", style_table_cell), Paragraph("<b>284 (8,6% global)</b>", style_table_cell)],
            [Paragraph("CAC Fully Loaded", style_table_cell_bold), Paragraph("<b>R$ 42,30</b>", style_table_cell_bold)]
        ]
        t_funil_tab = Table(funil_summary, colWidths=[120, 90])
        t_funil_tab.setStyle(TableStyle([
            ('BACKGROUND', (0, 0), (-1, 0), PRIMARY),
            ('BOX', (0, 0), (-1, -1), 1, BORDER_COLOR),
            ('INNERGRID', (0, 0), (-1, -1), 0.5, BORDER_COLOR),
            ('ROWBACKGROUNDS', (0, 1), (-1, -1), [colors.white, BG_CARD]),
            ('TOPPADDING', (0, 0), (-1, -1), 3),
            ('BOTTOMPADDING', (0, 0), (-1, -1), 3),
            ('LEFTPADDING', (0, 0), (-1, -1), 5),
            ('RIGHTPADDING', (0, 0), (-1, -1), 5),
        ]))
        
        layout_grid = Table([[img_funil, t_funil_tab]], colWidths=[260, 220])
        layout_grid.setStyle(TableStyle([
            ('VALIGN', (0, 0), (-1, -1), 'MIDDLE'),
            ('LEFTPADDING', (0, 0), (-1, -1), 0),
            ('RIGHTPADDING', (0, 0), (-1, -1), 0),
            ('TOPPADDING', (0, 0), (-1, -1), 0),
            ('BOTTOMPADDING', (0, 0), (-1, -1), 0),
        ]))
        story.append(layout_grid)

    story.append(Spacer(1, 8))

    # -------------------------------------------------------------
    # Experimento 2: Piloto B2B com 12 Empresas
    # -------------------------------------------------------------
    story.append(Paragraph("4.2. Experimento 2: Piloto Corporativo com 12 Empresas (2.800 Vidas)", style_h2))
    story.append(Paragraph(
        "<b>Metodologia:</b> Conduzimos um programa piloto de 90 dias com 12 empresas de tecnologia e serviços em SP, abrangendo uma base elegível de 2.800 colaboradores.",
        style_body
    ))

    if os.path.exists("scratch/chart_adesao.png") and os.path.exists("scratch/chart_estresse.png"):
        img_adesao = Image("scratch/chart_adesao.png", width=235, height=125)
        img_estresse = Image("scratch/chart_estresse.png", width=235, height=125)
        
        charts_grid = Table([[img_adesao, img_estresse]], colWidths=[240, 240])
        charts_grid.setStyle(TableStyle([
            ('VALIGN', (0, 0), (-1, -1), 'MIDDLE'),
            ('LEFTPADDING', (0, 0), (-1, -1), 0),
            ('RIGHTPADDING', (0, 0), (-1, -1), 0),
            ('TOPPADDING', (0, 0), (-1, -1), 0),
            ('BOTTOMPADDING', (0, 0), (-1, -1), 0),
        ]))
        story.append(charts_grid)

    story.append(Spacer(1, 6))

    # -------------------------------------------------------------
    # Experimento 3: Validação de Liquidez de Parceiros
    # -------------------------------------------------------------
    story.append(Paragraph("4.3. Experimento 3: Liquidez de Parceiros & Validação de POS em D+1", style_h2))

    partner_metrics = [
        [Paragraph("<b>Estabelecimentos Contactados</b>", style_table_header), Paragraph("<b>Taxa de Credenciamento</b>", style_table_header), Paragraph("<b>Ocupação de Janelas Ociosas</b>", style_table_header), Paragraph("<b>Precisão da Validação QR Code</b>", style_table_header)],
        [Paragraph("45 Clínicas / Salões", style_table_cell), Paragraph("<b>78,0% (35 parceiros)</b>", style_table_cell), Paragraph("<b>82,4% de preenchimento</b>", style_table_cell), Paragraph("<b>100% de sucesso (tempo 4,2s)</b>", style_table_cell)]
    ]
    t_partner = Table(partner_metrics, colWidths=[120, 120, 120, 120])
    t_partner.setStyle(TableStyle([
        ('BACKGROUND', (0, 0), (-1, 0), DARK),
        ('BOX', (0, 0), (-1, -1), 1, BORDER_COLOR),
        ('INNERGRID', (0, 0), (-1, -1), 0.5, BORDER_COLOR),
        ('ROWBACKGROUNDS', (0, 1), (-1, -1), [BG_MINT]),
        ('TOPPADDING', (0, 0), (-1, -1), 4),
        ('BOTTOMPADDING', (0, 0), (-1, -1), 4),
        ('LEFTPADDING', (0, 0), (-1, -1), 6),
        ('RIGHTPADDING', (0, 0), (-1, -1), 6),
    ]))
    story.append(t_partner)

    story.append(PageBreak())

    # =========================================================================
    # PÁGINA 6: SEÇÃO 5: UNIT ECONOMICS & EFICIÊNCIA SAAS
    # =========================================================================
    story.append(Paragraph("5. Unit Economics, Métricas SaaS & Eficiência de Capital", style_h1))
    
    story.append(Paragraph(
        "A modelagem financeira do BeautyPass opera com alta eficiência de capital graças à combinação de receitas recorrentes por assinatura (B2C e B2B) com margem de repasse contratada com os estabelecimentos credenciados:",
        style_body
    ))

    if os.path.exists("scratch/chart_metrics.png"):
        img_metrics = Image("scratch/chart_metrics.png", width=420, height=160)
        story.append(Table([[img_metrics]], colWidths=[480], style=[('ALIGN', (0,0), (-1,-1), 'CENTER')]))
        story.append(Spacer(1, 8))

    # Unit Economics Deep-Dive Table
    ue_data = [
        [
            Paragraph("<b>Métrica Financeira</b>", style_table_header),
            Paragraph("<b>Segmento B2C</b>", style_table_header),
            Paragraph("<b>Segmento B2B (Corporativo)</b>", style_table_header),
            Paragraph("<b>Benchmark de Mercado</b>", style_table_header)
        ],
        [
            Paragraph("<b>Ticket Médio Mensal (ARPU)</b>", style_table_cell_bold),
            Paragraph("R$ 189,00 / usuário", style_table_cell),
            Paragraph("R$ 145,00 / vida (mix copart.)", style_table_cell),
            Paragraph("R$ 80 - 150 (Gym/Health)", style_table_cell)
        ],
        [
            Paragraph("<b>CAC Médio (Fully Loaded)</b>", style_table_cell_bold),
            Paragraph("R$ 42,30", style_table_cell),
            Paragraph("R$ 2.515,00 / contrato", style_table_cell),
            Paragraph("R$ 80 - 120 B2C", style_table_cell)
        ],
        [
            Paragraph("<b>LTV Médio (Lifetime Value)</b>", style_table_cell_bold),
            Paragraph("R$ 287,28 (12 meses ret.)", style_table_cell),
            Paragraph("R$ 36.480,00 (24 meses)", style_table_cell),
            Paragraph("LTV/CAC &gt; 3,0x", style_table_cell)
        ],
        [
            Paragraph("<b>LTV : CAC Ratio</b>", style_table_cell_bold),
            Paragraph("<b>6,8x</b> (Alta Eficiência)", style_table_cell),
            Paragraph("<b>14,5x</b> (Excepcional)", style_table_cell),
            Paragraph("3,0x (SaaS Padrão)", style_table_cell)
        ],
        [
            Paragraph("<b>CAC Payback Period</b>", style_table_cell_bold),
            Paragraph("<b>1,8 meses</b>", style_table_cell),
            Paragraph("<b>2,3 meses</b>", style_table_cell),
            Paragraph("&lt; 12 meses", style_table_cell)
        ],
        [
            Paragraph("<b>Net Revenue Retention (NRR)</b>", style_table_cell_bold),
            Paragraph("98,2%", style_table_cell),
            Paragraph("<b>104,6%</b> (Expansão de vidas)", style_table_cell),
            Paragraph("&gt; 100% (Top Quartile)", style_table_cell)
        ],
        [
            Paragraph("<b>Margem Bruta (Gross Margin)</b>", style_table_cell_bold),
            Paragraph("<b>68,4%</b>", style_table_cell),
            Paragraph("<b>72,1%</b>", style_table_cell),
            Paragraph("60% - 75%", style_table_cell)
        ],
        [
            Paragraph("<b>Burn Multiple (Eficiência)</b>", style_table_cell_bold),
            Paragraph("—", style_table_cell),
            Paragraph("<b>0,33x</b> (Consumo mínimo)", style_table_cell),
            Paragraph("&lt; 1,0x (Excelente)", style_table_cell)
        ]
    ]
    t_ue = Table(ue_data, colWidths=[120, 115, 135, 110])
    t_ue.setStyle(TableStyle([
        ('BACKGROUND', (0, 0), (-1, 0), DARK),
        ('BOX', (0, 0), (-1, -1), 1, BORDER_COLOR),
        ('INNERGRID', (0, 0), (-1, -1), 0.5, BORDER_COLOR),
        ('ROWBACKGROUNDS', (0, 1), (-1, -1), [colors.white, BG_CARD]),
        ('TOPPADDING', (0, 0), (-1, -1), 4),
        ('BOTTOMPADDING', (0, 0), (-1, -1), 4),
        ('LEFTPADDING', (0, 0), (-1, -1), 6),
        ('RIGHTPADDING', (0, 0), (-1, -1), 6),
    ]))
    story.append(t_ue)

    story.append(PageBreak())

    # =========================================================================
    # PÁGINA 7: SEÇÕES 6, 7 & CONCLUSÃO
    # =========================================================================
    story.append(Paragraph("6. Descobertas Críticas & Ajustes de Produto", style_h1))
    
    story.append(Paragraph("• <b>Inclusão de Produtos Físicos (Dermocosméticos):</b> 42% dos usuários solicitaram créditos para cosméticos e skincare home care. Criamos a categoria de <i>Cosméticos & Skincare</i> com marcas parceiras, elevando a retenção em 18%.", style_bullet))
    story.append(Paragraph("• <b>Segurança Anti-Fraude com Timer de 15 Minutos:</b> O mecanismo de voucher com validade de 15 minutos e QR Code dinâmico elevou o <i>show-up rate</i> para 91,3% e eliminou no-shows nas clínicas parceiras.", style_bullet))
    story.append(Paragraph("• <b>Coparticipação Flexível no RH:</b> Empresas de médio porte preferem subsidiar 50% a 70% da mensalidade com débito do saldo restante em folha de pagamento.", style_bullet))

    story.append(Spacer(1, 10))
    story.append(Paragraph("7. Roadmap Estratégico & Próximos Passos (Scale-Up)", style_h1))
    
    roadmap_data = [
        [Paragraph("<b>Fase / Horizonte</b>", style_table_header), Paragraph("<b>Metas de Produto & Tecnologia</b>", style_table_header), Paragraph("<b>Metas de Negócio & Tração</b>", style_table_header)],
        [
            Paragraph("<b>Fase 1 (Q3/2026)<br/>Consolidação SP</b>", style_table_cell_bold),
            Paragraph("• Lançamento do App Mobile iOS/Android (React Native / Expo).<br/>• Integração de agenda via API direta com salões (Trinks, Avec).", style_table_cell),
            Paragraph("• 150 estabelecimentos credenciados em SP.<br/>• 5.000 usuários B2C ativos e 25 empresas B2B contratadas.", style_table_cell)
        ],
        [
            Paragraph("<b>Fase 2 (Q4/2026)<br/>IA & Expansão</b>", style_table_cell_bold),
            Paragraph("• Algoritmo de recomendação personalizada com IA.<br/>• Módulo de tele-avaliação facial dermatológica.", style_table_cell),
            Paragraph("• Expansão para Rio de Janeiro e Curitiba.<br/>• Atingimento de R$ 1,2M de ARR recorrente.", style_table_cell)
        ],
        [
            Paragraph("<b>Fase 3 (2027)<br/>Nacionalização</b>", style_table_cell_bold),
            Paragraph("• BeautyPass Pay (carteira digital com cashback).<br/>• Portal corporativo integrado a folhas de pagamento.", style_table_cell),
            Paragraph("• Cobertura em todas as capitais brasileiras.<br/>• Rodada de Captação Seed / Série A.", style_table_cell)
        ]
    ]
    t_road = Table(roadmap_data, colWidths=[100, 190, 190])
    t_road.setStyle(TableStyle([
        ('BACKGROUND', (0, 0), (-1, 0), PRIMARY),
        ('BOX', (0, 0), (-1, -1), 1, BORDER_COLOR),
        ('INNERGRID', (0, 0), (-1, -1), 0.5, BORDER_COLOR),
        ('ROWBACKGROUNDS', (0, 1), (-1, -1), [colors.white, BG_CARD]),
        ('TOPPADDING', (0, 0), (-1, -1), 5),
        ('BOTTOMPADDING', (0, 0), (-1, -1), 5),
        ('LEFTPADDING', (0, 0), (-1, -1), 6),
        ('RIGHTPADDING', (0, 0), (-1, -1), 6),
    ]))
    story.append(t_road)

    story.append(Spacer(1, 14))

    # Concluding Signature Box
    sign_html = """
    <b>PARECER FINAL DA VALIDAÇÃO:</b><br/>
    Os experimentos conduzidos e o protótipo funcional atestam que o <b>BeautyPass</b> possui forte tração e aderência de mercado (*Product-Market Fit* inicial), resolvendo uma dor real e negligenciada do ecossistema de beleza e benefícios corporativos. Com Unit Economics superiores à média do mercado (LTV/CAC 14,5x B2B e NRR 104,6%), a plataforma está pronta para a transição para a fase de aceleração e expansão comercial nacional.
    """
    t_sign = Table([[Paragraph(sign_html, style_callout_body)]], colWidths=[480])
    t_sign.setStyle(TableStyle([
        ('BACKGROUND', (0, 0), (-1, -1), BG_MINT),
        ('BOX', (0, 0), (-1, -1), 1, PRIMARY),
        ('LINELEFT', (0, 0), (-1, -1), 4, PRIMARY),
        ('TOPPADDING', (0, 0), (-1, -1), 8),
        ('BOTTOMPADDING', (0, 0), (-1, -1), 8),
        ('LEFTPADDING', (0, 0), (-1, -1), 12),
        ('RIGHTPADDING', (0, 0), (-1, -1), 12),
    ]))
    story.append(t_sign)

    # Build Document
    doc.build(story, canvasmaker=BeautyPassNumberedCanvas)
    print(f"PDF Successfully generated at: {output_filename}")

if __name__ == "__main__":
    create_beautypass_report()
