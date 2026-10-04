/**
 * BEAUTYPASS — APLICAÇÃO INTERATIVA: THE TRIPLE FUSION
 * Implementa toda a lógica de negócio, catálogo seed, navegação,
 * mostrador radial analógico-digital, mapa interativo, validação Luhn
 * e instrumentação de eventos da spec de validação.
 */

// ===================================================================
// 1. CATÁLOGO DE DADOS SEED (São Paulo — Pinheiros, Jardins, Itaim)
// ===================================================================
const MOCK_SALONS = [
  {
    "id": "s1",
    "name": "Ateliê Belle Époque",
    "neighborhood": "Pinheiros",
    "category": "hair",
    "rating": 4.9,
    "reviewsCount": 238,
    "distanceKm": 0.8,
    "lat": -23.5628,
    "lng": -46.6854,
    "address": "R. Fradique Coutinho, 980 - Pinheiros, São Paulo",
    "image": "https://images.unsplash.com/photo-1560066984-138dadb4c035?auto=format&fit=crop&w=800&q=80",
    "socialProof": "#Mariana agendou Escova há 12 min",
    "socialAvatar": "https://images.unsplash.com/photo-1494790108377-be9c29b29330?auto=format&fit=crop&w=100&h=100&q=80",
    "service": {
      "id": "srv_s1_1",
      "name": "Escova Modeladora & Nutrição",
      "durationMinutes": 45,
      "basePrice": 120,
      "category": "hair",
      "description": "Lavagem com produtos botânicos, hidratação profunda e escova modeladora de longa duração."
    },
    "staff": [
      {
        "id": "st1",
        "name": "Juliana Paes",
        "role": "Master Stylist",
        "rating": 4.95,
        "avatar": "https://images.unsplash.com/photo-1580489944761-15a19d654956?auto=format&fit=crop&w=120&q=80"
      },
      {
        "id": "st2",
        "name": "Rodrigo Faro",
        "role": "Visagista",
        "rating": 4.88,
        "avatar": "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?auto=format&fit=crop&w=120&q=80"
      }
    ],
    "reviews": [
      {
        "author": "Fernanda S.",
        "rating": 5,
        "comment": "Excelente atendimento, resultado incrível! Juliana é uma artista.",
        "date": "Set 2026"
      },
      {
        "author": "Ana R.",
        "rating": 5,
        "comment": "Já é minha profissional fixa. Super pontual e cuidadosa.",
        "date": "Set 2026"
      },
      {
        "author": "Camila T.",
        "rating": 4,
        "comment": "Ambiente agradável, mas demorou um pouco além do previsto.",
        "date": "Ago 2026"
      }
    ],
    "discountSlots": [
      {
        "time": "13:00",
        "discountPct": 30,
        "type": "economy"
      },
      {
        "time": "13:30",
        "discountPct": 30,
        "type": "economy"
      },
      {
        "time": "14:00",
        "discountPct": 35,
        "type": "economy"
      },
      {
        "time": "14:30",
        "discountPct": 30,
        "type": "economy"
      },
      {
        "time": "15:00",
        "discountPct": 25,
        "type": "economy"
      },
      {
        "time": "17:30",
        "discountPct": 30,
        "type": "urgent"
      }
    ],
    "services": [
      {
        "id": "srv_s1_1",
        "name": "Escova Modeladora & Nutrição",
        "durationMinutes": 45,
        "basePrice": 120,
        "category": "hair",
        "description": "Lavagem com produtos botânicos, hidratação profunda e escova modeladora de longa duração."
      },
      {
        "id": "srv_s1_2",
        "name": "Corte Visagista Feminino",
        "durationMinutes": 50,
        "basePrice": 140,
        "category": "hair",
        "description": "Lavagem especial, corte alinhado ao formato do rosto e secagem com proteção térmica."
      },
      {
        "id": "srv_s1_3",
        "name": "Hidratação Reconstrutora Moroccanoil",
        "durationMinutes": 40,
        "basePrice": 95,
        "category": "hair",
        "description": "Tratamento intensivo com óleo de argan para reposição lipídica e brilho imediato."
      },
      {
        "id": "srv_s1_4",
        "name": "Manicure & Spa de Cutículas",
        "durationMinutes": 40,
        "basePrice": 55,
        "category": "nails",
        "description": "Cuidado completo das unhas com esmaltação tradicional e esfoliação de mãos."
      },
      {
        "id": "srv_s1_5",
        "name": "Mechas & Luzes Pontuais Glow",
        "durationMinutes": 90,
        "basePrice": 260,
        "category": "hair",
        "description": "Iluminação estratégica dos fios com proteção plex e tonalização luminosa."
      }
    ],
    "leadStaff": {
      "name": "Juliana Paes Mendonça",
      "role": "Fundadora & Master Stylist",
      "avatar": "https://images.unsplash.com/photo-1573496359142-b8d87734a5a2?auto=format&fit=crop&w=200&h=200&q=80",
      "verified": true
    },
    "mutualNetwork": {
      "friendsCount": 3,
      "avatars": [
        "https://images.unsplash.com/photo-1494790108377-be9c29b29330?auto=format&fit=crop&w=100&h=100&q=80",
        "https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&w=100&h=100&q=80",
        "https://images.unsplash.com/photo-1517841905240-472988babdf9?auto=format&fit=crop&w=100&h=100&q=80"
      ],
      "text": "Mariana, Camila e Beatriz frequentam este espaço"
    }
  },
  {
    "id": "s2",
    "name": "Lumina Studio & Nail Bar",
    "neighborhood": "Pinheiros",
    "category": "nails",
    "rating": 4.8,
    "reviewsCount": 174,
    "distanceKm": 1.2,
    "lat": -23.5682,
    "lng": -46.6801,
    "address": "R. dos Pinheiros, 412 - Pinheiros, São Paulo",
    "image": "https://images.unsplash.com/photo-1633681926022-84c23e8cb2d6?auto=format&fit=crop&w=800&q=80",
    "socialProof": "#Larissa marcou Manicure Spa há 19 min",
    "socialAvatar": "https://images.unsplash.com/photo-1524504388940-b1c1722653e1?auto=format&fit=crop&w=100&h=100&q=80",
    "service": {
      "id": "srv_s2_1",
      "name": "Design de Sobrancelhas & Spa de Mãos",
      "durationMinutes": 50,
      "basePrice": 85,
      "category": "nails",
      "description": "Alinhamento com linha orgânica, esfoliação e hidratação com cera morna."
    },
    "staff": [
      {
        "id": "st3",
        "name": "Fernanda Lima",
        "role": "Nail Designer",
        "rating": 4.92,
        "avatar": "https://images.unsplash.com/photo-1573496359142-b8d87734a5a2?auto=format&fit=crop&w=120&q=80"
      },
      {
        "id": "st4",
        "name": "Carla Dias",
        "role": "Lash Designer",
        "rating": 4.85,
        "avatar": "https://images.unsplash.com/photo-1567532939604-b6b5b0db2604?auto=format&fit=crop&w=120&q=80"
      }
    ],
    "reviews": [
      {
        "author": "Bia M.",
        "rating": 5,
        "comment": "Melhor nail art que já fiz! Fernanda é incrível.",
        "date": "Set 2026"
      },
      {
        "author": "Lara K.",
        "rating": 4,
        "comment": "Ótimo atendimento, preços honestos e ambiente charmoso.",
        "date": "Set 2026"
      },
      {
        "author": "Sofia P.",
        "rating": 5,
        "comment": "Voltarei sempre! Design perfeito e muito durável.",
        "date": "Ago 2026"
      }
    ],
    "discountSlots": [
      {
        "time": "10:00",
        "discountPct": 25,
        "type": "economy"
      },
      {
        "time": "13:30",
        "discountPct": 30,
        "type": "economy"
      },
      {
        "time": "14:00",
        "discountPct": 30,
        "type": "economy"
      },
      {
        "time": "16:00",
        "discountPct": 20,
        "type": "urgent"
      }
    ],
    "services": [
      {
        "id": "srv_s2_1",
        "name": "Design de Sobrancelhas & Spa de Mãos",
        "durationMinutes": 50,
        "basePrice": 85,
        "category": "nails",
        "description": "Alinhamento com linha orgânica, esfoliação e hidratação com cera morna."
      },
      {
        "id": "srv_s2_2",
        "name": "Manicure Russa & Blindagem Gel",
        "durationMinutes": 60,
        "basePrice": 120,
        "category": "nails",
        "description": "Cuticulagem técnica a seco com micromotor e blindagem de alta resistência."
      },
      {
        "id": "srv_s2_3",
        "name": "Spa dos Pés & Reflexologia Express",
        "durationMinutes": 45,
        "basePrice": 75,
        "category": "nails",
        "description": "Esfoliação com sais marinhos, hidratação oclusiva e massagem podal relaxante."
      },
      {
        "id": "srv_s2_4",
        "name": "Lash Lifting de Cílios com Tintura",
        "durationMinutes": 50,
        "basePrice": 110,
        "category": "esthetic",
        "description": "Curvatura e pigmentação dos fios naturais para realçar o olhar."
      },
      {
        "id": "srv_s2_5",
        "name": "Esmaltação em Gel Longa Duração",
        "durationMinutes": 45,
        "basePrice": 65,
        "category": "nails",
        "description": "Secagem rápida em cabine LED com brilho espelhado por até 21 dias."
      }
    ],
    "leadStaff": {
      "name": "Beatriz Albuquerque",
      "role": "Nail Designer & Especialista em Gel",
      "avatar": "https://images.unsplash.com/photo-1580489944761-15a19d654956?auto=format&fit=crop&w=200&h=200&q=80",
      "verified": true
    },
    "mutualNetwork": {
      "friendsCount": 3,
      "avatars": [
        "https://images.unsplash.com/photo-1524504388940-b1c1722653e1?auto=format&fit=crop&w=100&h=100&q=80",
        "https://images.unsplash.com/photo-1544005313-94ddf0286df2?auto=format&fit=crop&w=100&h=100&q=80",
        "https://images.unsplash.com/photo-1529626455594-4ff0802cfb7e?auto=format&fit=crop&w=100&h=100&q=80"
      ],
      "text": "Larissa, Juliana e Sofia frequentam aqui com frequência"
    }
  },
  {
    "id": "s3",
    "name": "Serena Spa & Terapias",
    "neighborhood": "Jardins",
    "category": "massage",
    "rating": 4.9,
    "reviewsCount": 312,
    "distanceKm": 1.9,
    "lat": -23.5714,
    "lng": -46.6712,
    "address": "Al. Gabriel Monteiro da Silva, 1420 - Jardim Paulistano, SP",
    "image": "https://images.unsplash.com/photo-1540555700478-4be289fbecef?auto=format&fit=crop&w=800&q=80",
    "socialProof": "#Carolina reservou Massagem Relaxante há 8 min",
    "socialAvatar": "https://images.unsplash.com/photo-1548142813-c348350df52b?auto=format&fit=crop&w=100&h=100&q=80",
    "service": {
      "id": "srv_s3_1",
      "name": "Massagem Sueca com Óleos Essenciais",
      "durationMinutes": 60,
      "basePrice": 180,
      "category": "massage",
      "description": "Massagem terapêutica de corpo inteiro com aromaterapia de lavanda e alecrim."
    },
    "staff": [
      {
        "id": "st5",
        "name": "Alessandra M.",
        "role": "Fisioterapeuta",
        "rating": 4.98,
        "avatar": "https://images.unsplash.com/photo-1548142813-c348350df52b?auto=format&fit=crop&w=120&q=80"
      }
    ],
    "reviews": [
      {
        "author": "Clara V.",
        "rating": 5,
        "comment": "Saí completamente renovada! As mãos da Alessandra são mágicas.",
        "date": "Set 2026"
      },
      {
        "author": "Regina B.",
        "rating": 5,
        "comment": "Ambiente incrível, música relaxante e tratamento perfeito.",
        "date": "Set 2026"
      },
      {
        "author": "Patrícia N.",
        "rating": 4,
        "comment": "Muito boa experiência, voltarei com certeza.",
        "date": "Ago 2026"
      }
    ],
    "discountSlots": [
      {
        "time": "11:00",
        "discountPct": 30,
        "type": "economy"
      },
      {
        "time": "14:30",
        "discountPct": 35,
        "type": "economy"
      },
      {
        "time": "15:00",
        "discountPct": 30,
        "type": "economy"
      }
    ],
    "services": [
      {
        "id": "srv_s3_1",
        "name": "Massagem Sueca com Óleos Essenciais",
        "durationMinutes": 60,
        "basePrice": 180,
        "category": "massage",
        "description": "Massagem terapêutica de corpo inteiro com aromaterapia de lavanda e alecrim."
      },
      {
        "id": "srv_s3_2",
        "name": "Drenagem Linfática Desintoxicante",
        "durationMinutes": 50,
        "basePrice": 160,
        "category": "massage",
        "description": "Manobras suaves para redução imediata de retenção de líquidos e toxinas."
      },
      {
        "id": "srv_s3_3",
        "name": "Massagem Craniofacial & Alívio de Tensão",
        "durationMinutes": 35,
        "basePrice": 95,
        "category": "massage",
        "description": "Foco exclusivo em pontos de estresse na cabeça, pescoço e trapézio."
      },
      {
        "id": "srv_s3_4",
        "name": "Candle Massage com Velas Morna",
        "durationMinutes": 60,
        "basePrice": 195,
        "category": "massage",
        "description": "Manteiga vegetal cosmética aquecida para hidratação e relaxamento profundo."
      },
      {
        "id": "srv_s3_5",
        "name": "Shiatsu Integrativo",
        "durationMinutes": 60,
        "basePrice": 170,
        "category": "massage",
        "description": "Pressão nos meridianos para equilíbrio bioenergético e alívio de nós musculares."
      }
    ],
    "leadStaff": {
      "name": "Monique Soares",
      "role": "Terapeuta Ayurvédica & Spa Director",
      "avatar": "https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&w=200&h=200&q=80",
      "verified": true
    },
    "mutualNetwork": {
      "friendsCount": 3,
      "avatars": [
        "https://images.unsplash.com/photo-1548142813-c348350df52b?auto=format&fit=crop&w=100&h=100&q=80",
        "https://images.unsplash.com/photo-1567532939604-b6b5b0db2604?auto=format&fit=crop&w=100&h=100&q=80",
        "https://images.unsplash.com/photo-1508214751196-bcfd4ca60f91?auto=format&fit=crop&w=100&h=100&q=80"
      ],
      "text": "Carolina, Rafaela e Bianca recomendaram este spa"
    }
  },
  {
    "id": "s4",
    "name": "Dermacare Estética Facial",
    "neighborhood": "Itaim Bibi",
    "category": "facial",
    "rating": 4.85,
    "reviewsCount": 145,
    "distanceKm": 2.3,
    "lat": -23.5789,
    "lng": -46.6765,
    "address": "R. Amauri, 280 - Itaim Bibi, São Paulo",
    "image": "https://images.unsplash.com/photo-1629909613654-28e377c37b09?auto=format&fit=crop&w=800&q=80",
    "socialProof": "#Gabriela agendou Limpeza Facial há 15 min",
    "socialAvatar": "https://images.unsplash.com/photo-1531746020798-e6953c6e8e04?auto=format&fit=crop&w=100&h=100&q=80",
    "service": {
      "id": "srv_s4_1",
      "name": "Limpeza de Pele Ultrassônica & Peeling",
      "durationMinutes": 75,
      "basePrice": 210,
      "category": "esthetic",
      "description": "Remoção suave de impurezas com peeling de diamante e máscara calmante de camomila."
    },
    "staff": [
      {
        "id": "st6",
        "name": "Dra. Vanessa",
        "role": "Dermatologista Esteta",
        "rating": 4.96,
        "avatar": "https://images.unsplash.com/photo-1559839734-2b71ea197ec2?auto=format&fit=crop&w=120&q=80"
      }
    ],
    "reviews": [
      {
        "author": "Juliana F.",
        "rating": 5,
        "comment": "Dra. Vanessa é excepcional! Minha pele melhorou muito.",
        "date": "Set 2026"
      },
      {
        "author": "Tais O.",
        "rating": 4,
        "comment": "Excelente profissional, atendimento de primeira.",
        "date": "Ago 2026"
      }
    ],
    "discountSlots": [
      {
        "time": "09:30",
        "discountPct": 20,
        "type": "economy"
      },
      {
        "time": "13:00",
        "discountPct": 30,
        "type": "economy"
      },
      {
        "time": "13:45",
        "discountPct": 35,
        "type": "economy"
      }
    ],
    "services": [
      {
        "id": "srv_s4_1",
        "name": "Limpeza de Pele Ultrassônica & Peeling",
        "durationMinutes": 75,
        "basePrice": 210,
        "category": "esthetic",
        "description": "Remoção suave de impurezas com peeling de diamante e máscara calmante de camomila."
      },
      {
        "id": "srv_s4_2",
        "name": "Hidratação Oclusiva de Ácido Hialurônico",
        "durationMinutes": 45,
        "basePrice": 140,
        "category": "esthetic",
        "description": "Revitalização celular intensa para viço imediato e preenchimento de linhas finas."
      },
      {
        "id": "srv_s4_3",
        "name": "Revitalização Facial com Vitamina C Pura",
        "durationMinutes": 50,
        "basePrice": 165,
        "category": "esthetic",
        "description": "Ação antioxidante clareadora e estimulante de síntese de colágeno."
      },
      {
        "id": "srv_s4_4",
        "name": "Drenagem Linfática Facial Anti-Olheiras",
        "durationMinutes": 35,
        "basePrice": 90,
        "category": "esthetic",
        "description": "Desinchaço do contorno dos olhos e definição do contorno da mandíbula."
      },
      {
        "id": "srv_s4_5",
        "name": "Peeling Químico de Ácido Mandélico",
        "durationMinutes": 45,
        "basePrice": 180,
        "category": "esthetic",
        "description": "Renovação cutânea suave para equilíbrio de oleosidade e manchas solares."
      }
    ],
    "leadStaff": {
      "name": "Dra. Camila Nogueira",
      "role": "Médica Dermatologista (CRM/SP)",
      "avatar": "https://images.unsplash.com/photo-1559839734-2b71ea197ec2?auto=format&fit=crop&w=200&h=200&q=80",
      "verified": true
    },
    "mutualNetwork": {
      "friendsCount": 2,
      "avatars": [
        "https://images.unsplash.com/photo-1531746020798-e6953c6e8e04?auto=format&fit=crop&w=100&h=100&q=80",
        "https://images.unsplash.com/photo-1573496359142-b8d87734a5a2?auto=format&fit=crop&w=100&h=100&q=80"
      ],
      "text": "Gabriela e Paula realizam protocolos estéticos aqui"
    }
  },
  {
    "id": "s5",
    "name": "Barbearia Maestro",
    "neighborhood": "Vila Madalena",
    "category": "barber",
    "rating": 4.7,
    "reviewsCount": 189,
    "distanceKm": 2.1,
    "lat": -23.5555,
    "lng": -46.692,
    "address": "R. Aspicuelta, 78 - Vila Madalena, São Paulo",
    "image": "https://images.unsplash.com/photo-1503951914875-452162b0f3f1?auto=format&fit=crop&w=800&q=80",
    "socialProof": "#Lucas agendou Barboterapia há 22 min",
    "socialAvatar": "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?auto=format&fit=crop&w=100&h=100&q=80",
    "service": {
      "id": "srv_s5_1",
      "name": "Corte Masculino & Barba Tradicional",
      "durationMinutes": 40,
      "basePrice": 70,
      "category": "barber",
      "description": "Corte estilizado com navalha, barba na toalha quente e finalização com produtos premium."
    },
    "staff": [
      {
        "id": "st7",
        "name": "Marcus V.",
        "role": "Mestre Barbeiro",
        "rating": 4.85,
        "avatar": "https://images.unsplash.com/photo-1599566150163-29194dcaad36?auto=format&fit=crop&w=120&q=80"
      },
      {
        "id": "st8",
        "name": "Thiago R.",
        "role": "Barbeiro Senior",
        "rating": 4.72,
        "avatar": "https://images.unsplash.com/photo-1506794778202-cad84cf45f1d?auto=format&fit=crop&w=120&q=80"
      }
    ],
    "reviews": [
      {
        "author": "Ricardo G.",
        "rating": 5,
        "comment": "Melhor barbearia da Vila. Marcus é um artista!",
        "date": "Set 2026"
      },
      {
        "author": "Daniel M.",
        "rating": 5,
        "comment": "Ambiente retrô perfeito, corte impecável.",
        "date": "Set 2026"
      },
      {
        "author": "Felipe S.",
        "rating": 4,
        "comment": "Ótimo serviço. Às vezes a espera demora um pouco.",
        "date": "Ago 2026"
      }
    ],
    "discountSlots": [
      {
        "time": "09:00",
        "discountPct": 25,
        "type": "economy"
      },
      {
        "time": "13:00",
        "discountPct": 30,
        "type": "economy"
      },
      {
        "time": "14:00",
        "discountPct": 25,
        "type": "economy"
      },
      {
        "time": "17:00",
        "discountPct": 20,
        "type": "urgent"
      }
    ],
    "services": [
      {
        "id": "srv_s5_1",
        "name": "Corte Masculino & Barba Tradicional",
        "durationMinutes": 40,
        "basePrice": 70,
        "category": "barber",
        "description": "Corte estilizado com navalha, barba na toalha quente e finalização com produtos premium."
      },
      {
        "id": "srv_s5_2",
        "name": "Corte Cabelo Degradê Fade & Tesoura",
        "durationMinutes": 35,
        "basePrice": 50,
        "category": "barber",
        "description": "Degradê na navalha ou corte na tesoura clássico com lavagem mentolada refrescante."
      },
      {
        "id": "srv_s5_3",
        "name": "Barboterapia Completa com Vapor de Ozônio",
        "durationMinutes": 30,
        "basePrice": 45,
        "category": "barber",
        "description": "Toalha quente, vapor de ozônio, hidratação de barba e massagem facial relaxante."
      },
      {
        "id": "srv_s5_4",
        "name": "Selagem Capilar Masculina Redutora",
        "durationMinutes": 45,
        "basePrice": 85,
        "category": "barber",
        "description": "Alinhamento dos fios rebeldes sem formol e com brilho acetinado."
      },
      {
        "id": "srv_s5_5",
        "name": "Sobrancelha Masculina na Navalha",
        "durationMinutes": 15,
        "basePrice": 25,
        "category": "barber",
        "description": "Limpeza natural das sobrancelhas preservando os traços masculinos."
      }
    ],
    "leadStaff": {
      "name": "Marcos Vinicius Silva",
      "role": "Mestre Barbeiro & Visagista",
      "avatar": "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?auto=format&fit=crop&w=200&h=200&q=80",
      "verified": true
    },
    "mutualNetwork": {
      "friendsCount": 3,
      "avatars": [
        "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?auto=format&fit=crop&w=100&h=100&q=80",
        "https://images.unsplash.com/photo-1492562080023-ab3db95bfbce?auto=format&fit=crop&w=100&h=100&q=80",
        "https://images.unsplash.com/photo-1506794778202-cad84cf45f1d?auto=format&fit=crop&w=100&h=100&q=80"
      ],
      "text": "3 contatos da sua rede cortam o cabelo neste espaço"
    }
  },
  {
    "id": "s6",
    "name": "Arte Nail Studio",
    "neighborhood": "Consolação",
    "category": "nails",
    "rating": 4.6,
    "reviewsCount": 98,
    "distanceKm": 2.8,
    "lat": -23.554,
    "lng": -46.659,
    "address": "R. da Consolação, 2345 - Consolação, São Paulo",
    "image": "https://images.unsplash.com/photo-1604654894610-df63bc536371?auto=format&fit=crop&w=800&q=80",
    "socialProof": "#Sofia fez Alongamento em Gel há 31 min",
    "socialAvatar": "https://images.unsplash.com/photo-1529626455594-4ff0802cfb7e?auto=format&fit=crop&w=100&h=100&q=80",
    "service": {
      "id": "srv_s6_1",
      "name": "Nail Art Premium & Gel Glitter",
      "durationMinutes": 55,
      "basePrice": 95,
      "category": "nails",
      "description": "Aplicação de gel com nail art personalizado, glitter 3D e acabamento em top coat fosco ou brilhante."
    },
    "staff": [
      {
        "id": "st9",
        "name": "Tatiane Cruz",
        "role": "Nail Artist",
        "rating": 4.78,
        "avatar": "https://images.unsplash.com/photo-1531746020798-e6953c6e8e04?auto=format&fit=crop&w=120&q=80"
      }
    ],
    "reviews": [
      {
        "author": "Gabi L.",
        "rating": 5,
        "comment": "Tatiane é super criativa! Amei o resultado.",
        "date": "Set 2026"
      },
      {
        "author": "Nathalia P.",
        "rating": 4,
        "comment": "Nail art linda e durável. Recomendo muito!",
        "date": "Set 2026"
      }
    ],
    "discountSlots": [
      {
        "time": "10:30",
        "discountPct": 20,
        "type": "economy"
      },
      {
        "time": "14:00",
        "discountPct": 30,
        "type": "economy"
      },
      {
        "time": "15:30",
        "discountPct": 25,
        "type": "economy"
      },
      {
        "time": "17:30",
        "discountPct": 30,
        "type": "urgent"
      }
    ],
    "services": [
      {
        "id": "srv_s6_1",
        "name": "Nail Art Premium & Gel Glitter",
        "durationMinutes": 55,
        "basePrice": 95,
        "category": "nails",
        "description": "Aplicação de gel com nail art personalizado, glitter 3D e acabamento em top coat fosco ou brilhante."
      },
      {
        "id": "srv_s6_2",
        "name": "Manicure Clássica & Esmaltação Nacional",
        "durationMinutes": 40,
        "basePrice": 48,
        "category": "nails",
        "description": "Cuticulagem delicada e esmaltação tradicional com secagem rápida."
      },
      {
        "id": "srv_s6_3",
        "name": "Alongamento de Fibra de Vidro",
        "durationMinutes": 90,
        "basePrice": 190,
        "category": "nails",
        "description": "Extensão ultra natural com filamentos de fibra e acabamento em gel moldado."
      },
      {
        "id": "srv_s6_4",
        "name": "Banho de Gel Fortalecedor",
        "durationMinutes": 50,
        "basePrice": 80,
        "category": "nails",
        "description": "Camada protetora em unhas naturais para prevenir descamações e quebras."
      },
      {
        "id": "srv_s6_5",
        "name": "Pedicure Spa Esfoliante",
        "durationMinutes": 45,
        "basePrice": 60,
        "category": "nails",
        "description": "Cuidado completo dos pés com lixamento podal e hidratação nutritiva profunda."
      }
    ],
    "leadStaff": {
      "name": "Letícia Hashimoto",
      "role": "Nail Artist & Lash Designer",
      "avatar": "https://images.unsplash.com/photo-1517841905240-472988babdf9?auto=format&fit=crop&w=200&h=200&q=80",
      "verified": true
    },
    "mutualNetwork": {
      "friendsCount": 2,
      "avatars": [
        "https://images.unsplash.com/photo-1529626455594-4ff0802cfb7e?auto=format&fit=crop&w=100&h=100&q=80",
        "https://images.unsplash.com/photo-1517841905240-472988babdf9?auto=format&fit=crop&w=100&h=100&q=80"
      ],
      "text": "Sofia e Beatriz são clientes assíduas deste estúdio"
    }
  },
  {
    "id": "s7",
    "name": "Glow Skin & Beauty",
    "neighborhood": "Pinheiros",
    "category": "facial",
    "rating": 4.8,
    "reviewsCount": 127,
    "distanceKm": 1.5,
    "lat": -23.561,
    "lng": -46.682,
    "address": "R. Teodoro Sampaio, 1040 - Pinheiros, São Paulo",
    "image": "https://images.unsplash.com/photo-1570172619644-dfd03ed5d881?auto=format&fit=crop&w=800&q=80",
    "socialProof": "#Fernanda agendou Peeling de Diamante há 11 min",
    "socialAvatar": "https://images.unsplash.com/photo-1580489944761-15a19d654956?auto=format&fit=crop&w=100&h=100&q=80",
    "service": {
      "id": "srv_s7_1",
      "name": "Tratamento Skincare Personalizado",
      "durationMinutes": 60,
      "basePrice": 150,
      "category": "esthetic",
      "description": "Análise de pele, limpeza profunda, hidratação intensiva e proteção UV com produtos veganos."
    },
    "staff": [
      {
        "id": "st10",
        "name": "Bianca Costa",
        "role": "Esteticista Senior",
        "rating": 4.89,
        "avatar": "https://images.unsplash.com/photo-1487412720507-e7ab37603c6f?auto=format&fit=crop&w=120&q=80"
      },
      {
        "id": "st11",
        "name": "Laura Mendes",
        "role": "Especialista em Pele",
        "rating": 4.75,
        "avatar": "https://images.unsplash.com/photo-1489424731084-a5d8b219a5bb?auto=format&fit=crop&w=120&q=80"
      }
    ],
    "reviews": [
      {
        "author": "Renata A.",
        "rating": 5,
        "comment": "Minha pele nunca ficou tão bonita! Bianca é excepcional.",
        "date": "Set 2026"
      },
      {
        "author": "Larissa F.",
        "rating": 5,
        "comment": "Produtos de alta qualidade e atendimento acolhedor.",
        "date": "Set 2026"
      },
      {
        "author": "Amanda O.",
        "rating": 4,
        "comment": "Ótimo resultado, vou virar cliente fixa com certeza.",
        "date": "Ago 2026"
      }
    ],
    "discountSlots": [
      {
        "time": "11:00",
        "discountPct": 25,
        "type": "economy"
      },
      {
        "time": "14:00",
        "discountPct": 30,
        "type": "economy"
      },
      {
        "time": "15:00",
        "discountPct": 30,
        "type": "economy"
      },
      {
        "time": "17:00",
        "discountPct": 25,
        "type": "urgent"
      }
    ],
    "services": [
      {
        "id": "srv_s7_1",
        "name": "Tratamento Skincare Personalizado",
        "durationMinutes": 60,
        "basePrice": 150,
        "category": "esthetic",
        "description": "Análise de pele, limpeza profunda, hidratação intensiva e proteção UV com produtos veganos."
      },
      {
        "id": "srv_s7_2",
        "name": "Limpeza de Pele com Extração a Vácuo",
        "durationMinutes": 55,
        "basePrice": 130,
        "category": "esthetic",
        "description": "Remoção de cravos e impurezas por sucção delicada sem marcas avermelhadas."
      },
      {
        "id": "srv_s7_3",
        "name": "Máscara Hidroplástica Calmante de Calêndula",
        "durationMinutes": 40,
        "basePrice": 95,
        "category": "esthetic",
        "description": "Infusão biológica para regeneração de peles sensibilizadas pelo sol ou poluição."
      },
      {
        "id": "srv_s7_4",
        "name": "Massagem Modeladora Facial com Gua Sha",
        "durationMinutes": 35,
        "basePrice": 85,
        "category": "esthetic",
        "description": "Estímulo circulatório com pedras nobres de quartzo verde para efeito lifting."
      },
      {
        "id": "srv_s7_5",
        "name": "Microagulhamento com Fatores de Crescimento",
        "durationMinutes": 60,
        "basePrice": 220,
        "category": "esthetic",
        "description": "Indução percutânea de colágeno para firmeza cutânea e suavização de poros."
      }
    ],
    "leadStaff": {
      "name": "Dra. Viviane Guimarães",
      "role": "Biomédica Esteta & Cosmiatra",
      "avatar": "https://images.unsplash.com/photo-1594744803329-e58b31de8bf5?auto=format&fit=crop&w=200&h=200&q=80",
      "verified": true
    },
    "mutualNetwork": {
      "friendsCount": 3,
      "avatars": [
        "https://images.unsplash.com/photo-1580489944761-15a19d654956?auto=format&fit=crop&w=100&h=100&q=80",
        "https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&w=100&h=100&q=80",
        "https://images.unsplash.com/photo-1548142813-c348350df52b?auto=format&fit=crop&w=100&h=100&q=80"
      ],
      "text": "Fernanda, Camila e Carolina cuidam da pele aqui"
    }
  },
  {
    "id": "s8",
    "name": "Studio Mix Beleza & Bem-Estar",
    "neighborhood": "Perdizes",
    "category": "mixed",
    "rating": 4.75,
    "reviewsCount": 203,
    "distanceKm": 3.2,
    "lat": -23.5335,
    "lng": -46.669,
    "address": "R. Cardoso de Almeida, 542 - Perdizes, São Paulo",
    "image": "https://images.unsplash.com/photo-1522337360788-8b13dee7a37e?auto=format&fit=crop&w=800&q=80",
    "socialProof": "#Paula agendou Corte & Nutrição há 25 min",
    "socialAvatar": "https://images.unsplash.com/photo-1573496359142-b8d87734a5a2?auto=format&fit=crop&w=100&h=100&q=80",
    "service": {
      "id": "srv_s8_1",
      "name": "Combo Cabelo & Maquiagem",
      "durationMinutes": 90,
      "basePrice": 195,
      "category": "hair",
      "description": "Escova modeladora, maquiagem natural e finalização com penteado para eventos especiais."
    },
    "staff": [
      {
        "id": "st12",
        "name": "Vanessa Lima",
        "role": "Hair & Makeup Artist",
        "rating": 4.82,
        "avatar": "https://images.unsplash.com/photo-1524504388940-b1c1722653e1?auto=format&fit=crop&w=120&q=80"
      },
      {
        "id": "st13",
        "name": "Priscila Mota",
        "role": "Esteticista & Visagista",
        "rating": 4.7,
        "avatar": "https://images.unsplash.com/photo-1438761681033-6461ffad8d80?auto=format&fit=crop&w=120&q=80"
      }
    ],
    "reviews": [
      {
        "author": "Isabela G.",
        "rating": 5,
        "comment": "Perfeito para eventos! Saí completamente deslumbrante.",
        "date": "Set 2026"
      },
      {
        "author": "Monica R.",
        "rating": 4,
        "comment": "Muito bom atendimento e resultado excelente. Recomendo!",
        "date": "Ago 2026"
      }
    ],
    "discountSlots": [
      {
        "time": "10:00",
        "discountPct": 20,
        "type": "economy"
      },
      {
        "time": "13:00",
        "discountPct": 30,
        "type": "economy"
      },
      {
        "time": "14:00",
        "discountPct": 35,
        "type": "economy"
      },
      {
        "time": "16:00",
        "discountPct": 25,
        "type": "urgent"
      }
    ],
    "services": [
      {
        "id": "srv_s8_1",
        "name": "Combo Cabelo & Maquiagem",
        "durationMinutes": 90,
        "basePrice": 195,
        "category": "hair",
        "description": "Escova modeladora, maquiagem natural e finalização com penteado para eventos especiais."
      },
      {
        "id": "srv_s8_2",
        "name": "Escova Lisa com Chapinha Cerâmica",
        "durationMinutes": 45,
        "basePrice": 80,
        "category": "hair",
        "description": "Lavagem com shampoo purificante e acabamento extra liso polido."
      },
      {
        "id": "srv_s8_3",
        "name": "Penteado Semi-Preso para Festas",
        "durationMinutes": 50,
        "basePrice": 110,
        "category": "hair",
        "description": "Tranças, babyliss com ondas soltas ou coques despojados com fixação flexível."
      },
      {
        "id": "srv_s8_4",
        "name": "Design de Sobrancelhas com Henna Orgânica",
        "durationMinutes": 40,
        "basePrice": 55,
        "category": "esthetic",
        "description": "Definição e preenchimento de falhas do olhar com tintura vegetal hipoalergênica."
      },
      {
        "id": "srv_s8_5",
        "name": "Spa de Mãos & Pés Simultâneo",
        "durationMinutes": 60,
        "basePrice": 115,
        "category": "nails",
        "description": "Agilidade e cuidado premium para mãos e pés realizados simultaneamente por duas profissionais."
      }
    ],
    "leadStaff": {
      "name": "Sabrina Sato Oliveira",
      "role": "Hair Concept & Visagista",
      "avatar": "https://images.unsplash.com/photo-1544005313-94ddf0286df2?auto=format&fit=crop&w=200&h=200&q=80",
      "verified": true
    },
    "mutualNetwork": {
      "friendsCount": 2,
      "avatars": [
        "https://images.unsplash.com/photo-1573496359142-b8d87734a5a2?auto=format&fit=crop&w=100&h=100&q=80",
        "https://images.unsplash.com/photo-1494790108377-be9c29b29330?auto=format&fit=crop&w=100&h=100&q=80"
      ],
      "text": "Paula e Mariana avaliaram este salão com 5 estrelas"
    }
  },
  {
    "id": "s9",
    "name": "Vintage Barber Club",
    "neighborhood": "Pinheiros",
    "category": "barber",
    "rating": 4.85,
    "reviewsCount": 164,
    "distanceKm": 1.1,
    "lat": -23.5645,
    "lng": -46.688,
    "address": "R. Mourato Coelho, 612 - Pinheiros, São Paulo",
    "image": "https://images.unsplash.com/photo-1585747860715-2ba37e788b70?auto=format&fit=crop&w=800&q=80",
    "socialProof": "#Gabriel agendou Fade & Barba há 14 min",
    "socialAvatar": "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?auto=format&fit=crop&w=100&h=100&q=80",
    "service": {
      "id": "srv_s9_1",
      "name": "Corte Executivo & Camuflagem de Barba",
      "durationMinutes": 45,
      "basePrice": 85,
      "category": "barber",
      "description": "Corte refinado na tesoura, alinhamento de barba com vapor de ozônio e tônico fortalecedor."
    },
    "staff": [
      {
        "id": "st14",
        "name": "Leandro Torres",
        "role": "Barbeiro Especialista",
        "rating": 4.9,
        "avatar": "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?auto=format&fit=crop&w=120&q=80"
      }
    ],
    "reviews": [
      {
        "author": "Guilherme P.",
        "rating": 5,
        "comment": "Atendimento impecável! Leandro manja demais de corte clássico.",
        "date": "Set 2026"
      },
      {
        "author": "Marcelo T.",
        "rating": 5,
        "comment": "Pontualidade britânica e cerveja gelada inclusa.",
        "date": "Set 2026"
      }
    ],
    "discountSlots": [
      {
        "time": "11:30",
        "discountPct": 20,
        "type": "economy"
      },
      {
        "time": "14:00",
        "discountPct": 30,
        "type": "economy"
      },
      {
        "time": "17:30",
        "discountPct": 25,
        "type": "urgent"
      }
    ],
    "services": [
      {
        "id": "srv_s9_1",
        "name": "Corte Executivo & Camuflagem de Barba",
        "durationMinutes": 45,
        "basePrice": 85,
        "category": "barber",
        "description": "Corte refinado na tesoura, alinhamento de barba com vapor de ozônio e tônico fortalecedor."
      },
      {
        "id": "srv_s9_2",
        "name": "Corte Cabelo Clássico",
        "durationMinutes": 35,
        "basePrice": 55,
        "category": "barber",
        "description": "Tesoura ou máquina com acabamento de pezinho e costeletas na lâmina navalhete."
      },
      {
        "id": "srv_s9_3",
        "name": "Barboterapia Tradicional com Toalha Quente",
        "durationMinutes": 30,
        "basePrice": 45,
        "category": "barber",
        "description": "Toalha quente com essência de menta e óleo amaciante anti-irritação."
      },
      {
        "id": "srv_s9_4",
        "name": "Hidratação Capilar Masculina",
        "durationMinutes": 30,
        "basePrice": 50,
        "category": "barber",
        "description": "Nutrição profunda para fios secos e alívio de descamações do couro cabeludo."
      },
      {
        "id": "srv_s9_5",
        "name": "Acabamento Rápido de Pezinho e Barba",
        "durationMinutes": 15,
        "basePrice": 25,
        "category": "barber",
        "description": "Manutenção rápida dos contornos entre visitas completas ao barbeiro."
      }
    ],
    "leadStaff": {
      "name": "Ricardo Telles",
      "role": "Grooming Specialist & Master Barber",
      "avatar": "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?auto=format&fit=crop&w=200&h=200&q=80",
      "verified": true
    },
    "mutualNetwork": {
      "friendsCount": 2,
      "avatars": [
        "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?auto=format&fit=crop&w=100&h=100&q=80",
        "https://images.unsplash.com/photo-1519085360753-af0119f7cbe7?auto=format&fit=crop&w=100&h=100&q=80"
      ],
      "text": "Amigos da sua região indicam o Ricardo para corte clássico"
    }
  },
  {
    "id": "s10",
    "name": "Espaço Capelli D'Oro",
    "neighborhood": "Jardins",
    "category": "hair",
    "rating": 4.92,
    "reviewsCount": 310,
    "distanceKm": 2,
    "lat": -23.568,
    "lng": -46.666,
    "address": "R. Oscar Freire, 1120 - Cerqueira César, São Paulo",
    "image": "https://images.unsplash.com/photo-1562322140-8baeececf3df?auto=format&fit=crop&w=800&q=80",
    "socialProof": "#Rafaela agendou Balayage Glow há 40 min",
    "socialAvatar": "https://images.unsplash.com/photo-1567532939604-b6b5b0db2604?auto=format&fit=crop&w=100&h=100&q=80",
    "service": {
      "id": "srv_s10_1",
      "name": "Corte Visagista & Escova Glow",
      "durationMinutes": 60,
      "basePrice": 160,
      "category": "hair",
      "description": "Consultoria de visagismo facial, corte personalizado e finalização com protetor térmico orgânico."
    },
    "staff": [
      {
        "id": "st15",
        "name": "Claudio Mantovani",
        "role": "Diretor Criativo",
        "rating": 4.96,
        "avatar": "https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?auto=format&fit=crop&w=120&q=80"
      }
    ],
    "reviews": [
      {
        "author": "Marina C.",
        "rating": 5,
        "comment": "O Claudio transformou minha autoestima. Corte mais elegante que já fiz.",
        "date": "Set 2026"
      },
      {
        "author": "Debora S.",
        "rating": 5,
        "comment": "Ambiente luxuoso e atendimento impecável.",
        "date": "Ago 2026"
      }
    ],
    "discountSlots": [
      {
        "time": "13:00",
        "discountPct": 35,
        "type": "economy"
      },
      {
        "time": "13:45",
        "discountPct": 30,
        "type": "economy"
      },
      {
        "time": "16:30",
        "discountPct": 25,
        "type": "urgent"
      }
    ],
    "services": [
      {
        "id": "srv_s10_1",
        "name": "Corte Visagista & Escova Glow",
        "durationMinutes": 60,
        "basePrice": 160,
        "category": "hair",
        "description": "Consultoria de visagismo facial, corte personalizado e finalização com protetor térmico orgânico."
      },
      {
        "id": "srv_s10_2",
        "name": "Terapia Capilar Antiqueda & Ozonioterapia",
        "durationMinutes": 50,
        "basePrice": 150,
        "category": "hair",
        "description": "Desobstrução folicular profunda com vapor e laser infravermelho estimulante."
      },
      {
        "id": "srv_s10_3",
        "name": "Hidratação Kérastase Fusio-Dose",
        "durationMinutes": 45,
        "basePrice": 170,
        "category": "hair",
        "description": "Tratamento sob medida com concentrado e booster de alta performance para cabelos sensibilizados."
      },
      {
        "id": "srv_s10_4",
        "name": "Escova Modeladora com Babyliss Ondas",
        "durationMinutes": 50,
        "basePrice": 110,
        "category": "hair",
        "description": "Ondas volumosas e duradouras com fixação sedosa sem ressecar os fios."
      },
      {
        "id": "srv_s10_5",
        "name": "Matização e Banho de Brilho",
        "durationMinutes": 45,
        "basePrice": 130,
        "category": "hair",
        "description": "Neutralização de reflexos indesejados em cabelos loiros e mechas com nutrição."
      }
    ],
    "leadStaff": {
      "name": "Patrícia Kallas",
      "role": "Master Balayage & Morenas Iluminadas",
      "avatar": "https://images.unsplash.com/photo-1567532939604-b6b5b0db2604?auto=format&fit=crop&w=200&h=200&q=80",
      "verified": true
    },
    "mutualNetwork": {
      "friendsCount": 3,
      "avatars": [
        "https://images.unsplash.com/photo-1567532939604-b6b5b0db2604?auto=format&fit=crop&w=100&h=100&q=80",
        "https://images.unsplash.com/photo-1544005313-94ddf0286df2?auto=format&fit=crop&w=100&h=100&q=80",
        "https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&w=100&h=100&q=80"
      ],
      "text": "Rafaela, Juliana e Camila fazem mechas aqui"
    }
  },
  {
    "id": "s11",
    "name": "Esmalteria Petit Spa",
    "neighborhood": "Itaim Bibi",
    "category": "nails",
    "rating": 4.78,
    "reviewsCount": 156,
    "distanceKm": 2.5,
    "lat": -23.584,
    "lng": -46.678,
    "address": "R. Joaquim Floriano, 871 - Itaim Bibi, São Paulo",
    "image": "https://images.unsplash.com/photo-1519014816548-bf5fe059798b?auto=format&fit=crop&w=800&q=80",
    "socialProof": "#Bianca marcou Esmaltação em Gel há 6 min",
    "socialAvatar": "https://images.unsplash.com/photo-1508214751196-bcfd4ca60f91?auto=format&fit=crop&w=100&h=100&q=80",
    "service": {
      "id": "srv_s11_1",
      "name": "Manicure Russa Combinada & Esmaltação Gel",
      "durationMinutes": 60,
      "basePrice": 110,
      "category": "nails",
      "description": "Cuticulagem a seco com micromotor, blindagem de queratina e cor durável por 20 dias."
    },
    "staff": [
      {
        "id": "st16",
        "name": "Tatiana Smirnova",
        "role": "Nail Master",
        "rating": 4.91,
        "avatar": "https://images.unsplash.com/photo-1580489944761-15a19d654956?auto=format&fit=crop&w=120&q=80"
      }
    ],
    "reviews": [
      {
        "author": "Bruna M.",
        "rating": 5,
        "comment": "A manicure russa da Tatiana é a mais perfeita de SP!",
        "date": "Set 2026"
      },
      {
        "author": "Carla V.",
        "rating": 4,
        "comment": "Dura muito tempo sem descascar, vale cada centavo.",
        "date": "Set 2026"
      }
    ],
    "discountSlots": [
      {
        "time": "10:30",
        "discountPct": 25,
        "type": "economy"
      },
      {
        "time": "14:30",
        "discountPct": 30,
        "type": "economy"
      }
    ],
    "services": [
      {
        "id": "srv_s11_1",
        "name": "Manicure Russa Combinada & Esmaltação Gel",
        "durationMinutes": 60,
        "basePrice": 110,
        "category": "nails",
        "description": "Cuticulagem a seco com micromotor, blindagem de queratina e cor durável por 20 dias."
      },
      {
        "id": "srv_s11_2",
        "name": "Esmaltação Simples Mãos com Lixamento",
        "durationMinutes": 35,
        "basePrice": 45,
        "category": "nails",
        "description": "Lixamento técnico, retirada suave de pelinhas e esmalte premium de alta cobertura."
      },
      {
        "id": "srv_s11_3",
        "name": "Spa dos Pés com Parafina Líquida",
        "durationMinutes": 50,
        "basePrice": 85,
        "category": "nails",
        "description": "Amolecimento de asperezas e calosidades com botas térmicas e nutrição intensiva."
      },
      {
        "id": "srv_s11_4",
        "name": "Francesinha Reversa em Gel Estruturado",
        "durationMinutes": 70,
        "basePrice": 130,
        "category": "nails",
        "description": "Técnica artística avançada com borda livre duradoura e simetria perfeita."
      },
      {
        "id": "srv_s11_5",
        "name": "Remoção Segura de Alongamento sem Danos",
        "durationMinutes": 40,
        "basePrice": 50,
        "category": "nails",
        "description": "Retirada mecânica cuidadosa preservando 100% da integridade da lâmina ungueal."
      }
    ],
    "leadStaff": {
      "name": "Vanessa Dumont",
      "role": "Esmaltação em Gel & Cutilagem Russa",
      "avatar": "https://images.unsplash.com/photo-1524504388940-b1c1722653e1?auto=format&fit=crop&w=200&h=200&q=80",
      "verified": true
    },
    "mutualNetwork": {
      "friendsCount": 3,
      "avatars": [
        "https://images.unsplash.com/photo-1508214751196-bcfd4ca60f91?auto=format&fit=crop&w=100&h=100&q=80",
        "https://images.unsplash.com/photo-1529626455594-4ff0802cfb7e?auto=format&fit=crop&w=100&h=100&q=80",
        "https://images.unsplash.com/photo-1548142813-c348350df52b?auto=format&fit=crop&w=100&h=100&q=80"
      ],
      "text": "Bianca, Sofia e Carolina fazem manutenção de unhas aqui"
    }
  },
  {
    "id": "s12",
    "name": "Lotus Terapias & Spa",
    "neighborhood": "Vila Madalena",
    "category": "massage",
    "rating": 4.88,
    "reviewsCount": 220,
    "distanceKm": 1.8,
    "lat": -23.551,
    "lng": -46.6905,
    "address": "R. Harmonia, 340 - Vila Madalena, São Paulo",
    "image": "https://images.unsplash.com/photo-1544161515-4ab6ce6db874?auto=format&fit=crop&w=800&q=80",
    "socialProof": "#Mariana fez Massagem com Pedras Quentes há 17 min",
    "socialAvatar": "https://images.unsplash.com/photo-1494790108377-be9c29b29330?auto=format&fit=crop&w=100&h=100&q=80",
    "service": {
      "id": "srv_s12_1",
      "name": "Massagem com Pedras Quentes Vulcânicas",
      "durationMinutes": 70,
      "basePrice": 195,
      "category": "massage",
      "description": "Termoterapia profunda com pedras de basalto aquecidas para alívio imediato de tensão."
    },
    "staff": [
      {
        "id": "st17",
        "name": "Miriam Tanaka",
        "role": "Terapeuta Corporal",
        "rating": 4.94,
        "avatar": "https://images.unsplash.com/photo-1544005313-94ddf0286df2?auto=format&fit=crop&w=120&q=80"
      }
    ],
    "reviews": [
      {
        "author": "Luciana R.",
        "rating": 5,
        "comment": "Lugar de paz absoluta no meio da correria de SP.",
        "date": "Set 2026"
      },
      {
        "author": "Andreia K.",
        "rating": 5,
        "comment": "As dores nas costas sumiram completamente. Recomendo muito!",
        "date": "Ago 2026"
      }
    ],
    "discountSlots": [
      {
        "time": "11:00",
        "discountPct": 30,
        "type": "economy"
      },
      {
        "time": "15:00",
        "discountPct": 35,
        "type": "economy"
      }
    ],
    "services": [
      {
        "id": "srv_s12_1",
        "name": "Massagem com Pedras Quentes Vulcânicas",
        "durationMinutes": 70,
        "basePrice": 195,
        "category": "massage",
        "description": "Termoterapia profunda com pedras de basalto aquecidas para alívio imediato de tensão."
      },
      {
        "id": "srv_s12_2",
        "name": "Drenagem Corporal Anti-Inchaço",
        "durationMinutes": 50,
        "basePrice": 150,
        "category": "massage",
        "description": "Estimulação do sistema linfático com manobras rítmicas e óleos botânicos."
      },
      {
        "id": "srv_s12_3",
        "name": "Quick Massage Express na Cadeira Ergonômica",
        "durationMinutes": 25,
        "basePrice": 65,
        "category": "massage",
        "description": "Alívio rápido de dores cervicais e lombares perfeito para pausas no meio da rotina."
      },
      {
        "id": "srv_s12_4",
        "name": "Massagem Terapêutica Desportiva",
        "durationMinutes": 60,
        "basePrice": 180,
        "category": "massage",
        "description": "Liberação miofascial com pressão firme para alívio de fadiga muscular pós-treino."
      },
      {
        "id": "srv_s12_5",
        "name": "Banho de Imersão Relaxante com Sais e Ervas",
        "durationMinutes": 40,
        "basePrice": 120,
        "category": "massage",
        "description": "Banheira aromaterápica em ofurô com extratos calmantes e cromoterapia."
      }
    ],
    "leadStaff": {
      "name": "Clara Fontana",
      "role": "Terapeuta Holística & Aromaterapeuta",
      "avatar": "https://images.unsplash.com/photo-1508214751196-bcfd4ca60f91?auto=format&fit=crop&w=200&h=200&q=80",
      "verified": true
    },
    "mutualNetwork": {
      "friendsCount": 2,
      "avatars": [
        "https://images.unsplash.com/photo-1494790108377-be9c29b29330?auto=format&fit=crop&w=100&h=100&q=80",
        "https://images.unsplash.com/photo-1580489944761-15a19d654956?auto=format&fit=crop&w=100&h=100&q=80"
      ],
      "text": "Mariana e Beatriz elogiaram a calma deste espaço"
    }
  },
  {
    "id": "s13",
    "name": "Pureza Estética Avançada",
    "neighborhood": "Perdizes",
    "category": "facial",
    "rating": 4.7,
    "reviewsCount": 112,
    "distanceKm": 3.5,
    "lat": -23.538,
    "lng": -46.674,
    "address": "R. Monte Alegre, 980 - Perdizes, São Paulo",
    "image": "https://images.unsplash.com/photo-1512290900672-1f02a64c483a?auto=format&fit=crop&w=800&q=80",
    "socialProof": "#Paula agendou Microagulhamento há 23 min",
    "socialAvatar": "https://images.unsplash.com/photo-1573496359142-b8d87734a5a2?auto=format&fit=crop&w=100&h=100&q=80",
    "service": {
      "id": "srv_s13_1",
      "name": "Revitalização Facial & Máscara de Ouro",
      "durationMinutes": 60,
      "basePrice": 175,
      "category": "esthetic",
      "description": "Esfoliação com ácido glicólico suave e hidratação com partículas biominerais luminosas."
    },
    "staff": [
      {
        "id": "st18",
        "name": "Dra. Carolina Rios",
        "role": "Biomédica Esteta",
        "rating": 4.86,
        "avatar": "https://images.unsplash.com/photo-1559839734-2b71ea197ec2?auto=format&fit=crop&w=120&q=80"
      }
    ],
    "reviews": [
      {
        "author": "Monica T.",
        "rating": 5,
        "comment": "Pele viçosa e luminosa logo após a sessão.",
        "date": "Set 2026"
      }
    ],
    "discountSlots": [
      {
        "time": "13:30",
        "discountPct": 25,
        "type": "economy"
      },
      {
        "time": "16:00",
        "discountPct": 30,
        "type": "urgent"
      }
    ],
    "services": [
      {
        "id": "srv_s13_1",
        "name": "Revitalização Facial & Máscara de Ouro",
        "durationMinutes": 60,
        "basePrice": 175,
        "category": "esthetic",
        "description": "Esfoliação com ácido glicólico suave e hidratação com partículas biominerais luminosas."
      },
      {
        "id": "srv_s13_2",
        "name": "Peeling de Diamante com Máscara de Colágeno",
        "durationMinutes": 50,
        "basePrice": 145,
        "category": "esthetic",
        "description": "Microdermoabrasão superficial para uniformizar textura e poros dilatados."
      },
      {
        "id": "srv_s13_3",
        "name": "Limpeza de Pele Profunda Tradicional",
        "durationMinutes": 70,
        "basePrice": 160,
        "category": "esthetic",
        "description": "Emoliência térmica, extração manual sem cicatrizes e alta frequência antibacteriana."
      },
      {
        "id": "srv_s13_4",
        "name": "Radiofrequência Facial Efeito Cinderela",
        "durationMinutes": 45,
        "basePrice": 190,
        "category": "esthetic",
        "description": "Aquecimento controlado das camadas dérmicas para retração imediata da flacidez."
      },
      {
        "id": "srv_s13_5",
        "name": "Clareamento de Axilas e Virilhas com Peeling",
        "durationMinutes": 40,
        "basePrice": 90,
        "category": "depilation",
        "description": "Protocolo despigmentante com ácidos suaves formulado especificamente para áreas sensíveis."
      }
    ],
    "leadStaff": {
      "name": "Dra. Danielle Meirelles",
      "role": "Farmacêutica Esteta Especialista em Peelings",
      "avatar": "https://images.unsplash.com/photo-1573497019940-1c28c88b4f3e?auto=format&fit=crop&w=200&h=200&q=80",
      "verified": true
    },
    "mutualNetwork": {
      "friendsCount": 2,
      "avatars": [
        "https://images.unsplash.com/photo-1573496359142-b8d87734a5a2?auto=format&fit=crop&w=100&h=100&q=80",
        "https://images.unsplash.com/photo-1531746020798-e6953c6e8e04?auto=format&fit=crop&w=100&h=100&q=80"
      ],
      "text": "Paula e Gabriela são atendidas pela Dra. Danielle"
    }
  },
  {
    "id": "s14",
    "name": "Barbearia República",
    "neighborhood": "Consolação",
    "category": "barber",
    "rating": 4.65,
    "reviewsCount": 138,
    "distanceKm": 2.9,
    "lat": -23.5505,
    "lng": -46.654,
    "address": "R. Augusta, 1420 - Consolação, São Paulo",
    "image": "https://images.unsplash.com/photo-1599351431202-1e0f0137899a?auto=format&fit=crop&w=800&q=80",
    "socialProof": "#Matheus agendou Barba Clássica há 10 min",
    "socialAvatar": "https://images.unsplash.com/photo-1492562080023-ab3db95bfbce?auto=format&fit=crop&w=100&h=100&q=80",
    "service": {
      "id": "srv_s14_1",
      "name": "Corte Clássico na Tesoura & Barba",
      "durationMinutes": 45,
      "basePrice": 65,
      "category": "barber",
      "description": "Estilo clássico e despojado na tesoura e navalha com finalização em pomada matte."
    },
    "staff": [
      {
        "id": "st19",
        "name": "Otavio Lima",
        "role": "Barbeiro Tradicional",
        "rating": 4.79,
        "avatar": "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?auto=format&fit=crop&w=120&q=80"
      }
    ],
    "reviews": [
      {
        "author": "Eduardo F.",
        "rating": 5,
        "comment": "Rápido, pontual e muito gente fina. Recomendo!",
        "date": "Set 2026"
      }
    ],
    "discountSlots": [
      {
        "time": "10:00",
        "discountPct": 20,
        "type": "economy"
      },
      {
        "time": "14:00",
        "discountPct": 30,
        "type": "economy"
      },
      {
        "time": "17:00",
        "discountPct": 20,
        "type": "urgent"
      }
    ],
    "services": [
      {
        "id": "srv_s14_1",
        "name": "Corte Clássico na Tesoura & Barba",
        "durationMinutes": 45,
        "basePrice": 65,
        "category": "barber",
        "description": "Estilo clássico e despojado na tesoura e navalha com finalização em pomada matte."
      },
      {
        "id": "srv_s14_2",
        "name": "Corte de Cabelo Máquina e Tesoura",
        "durationMinutes": 30,
        "basePrice": 45,
        "category": "barber",
        "description": "Corte ágil e prático para o dia a dia com lavagem refrescante inclusa."
      },
      {
        "id": "srv_s14_3",
        "name": "Barba Modelada na Navalha Tradicional",
        "durationMinutes": 25,
        "basePrice": 35,
        "category": "barber",
        "description": "Alinhamento preciso das linhas das bochechas e pescoço com óleo hidratante vegetal."
      },
      {
        "id": "srv_s14_4",
        "name": "Camuflagem de Fios Brancos Masculina",
        "durationMinutes": 35,
        "basePrice": 60,
        "category": "barber",
        "description": "Tonalização sutil e discreta para amenizar cabelos e barba grisalhos sem aspecto pintado."
      },
      {
        "id": "srv_s14_5",
        "name": "Higienização e Esfoliação Facial Masculina",
        "durationMinutes": 30,
        "basePrice": 40,
        "category": "esthetic",
        "description": "Limpeza facial rápida com sabonete antioleosidade, esfoliação suave e hidratação."
      }
    ],
    "leadStaff": {
      "name": "Thiago Barone",
      "role": "Barbeiro Tradicional & Visagista",
      "avatar": "https://images.unsplash.com/photo-1492562080023-ab3db95bfbce?auto=format&fit=crop&w=200&h=200&q=80",
      "verified": true
    },
    "mutualNetwork": {
      "friendsCount": 3,
      "avatars": [
        "https://images.unsplash.com/photo-1492562080023-ab3db95bfbce?auto=format&fit=crop&w=100&h=100&q=80",
        "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?auto=format&fit=crop&w=100&h=100&q=80",
        "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?auto=format&fit=crop&w=100&h=100&q=80"
      ],
      "text": "Excelente recomendação para cortes urbanos no centro"
    }
  },
  {
    "id": "s15",
    "name": "Velvet Hair Design",
    "neighborhood": "Vila Madalena",
    "category": "hair",
    "rating": 4.83,
    "reviewsCount": 195,
    "distanceKm": 2.2,
    "lat": -23.557,
    "lng": -46.695,
    "address": "R. Girassol, 210 - Vila Madalena, São Paulo",
    "image": "https://images.unsplash.com/photo-1521590832167-7bcbfaa6381f?auto=format&fit=crop&w=800&q=80",
    "socialProof": "#Larissa agendou Corte Orgânico há 5 min",
    "socialAvatar": "https://images.unsplash.com/photo-1524504388940-b1c1722653e1?auto=format&fit=crop&w=100&h=100&q=80",
    "service": {
      "id": "srv_s15_1",
      "name": "Corte Moderno Bob & Nutrição Lipídica",
      "durationMinutes": 50,
      "basePrice": 135,
      "category": "hair",
      "description": "Alinhamento de fios, corte moderno e nutrição à base de óleos nobres."
    },
    "staff": [
      {
        "id": "st20",
        "name": "Renata Vasconcellos",
        "role": "Hair Stylist",
        "rating": 4.9,
        "avatar": "https://images.unsplash.com/photo-1573496359142-b8d87734a5a2?auto=format&fit=crop&w=120&q=80"
      }
    ],
    "reviews": [
      {
        "author": "Priscila N.",
        "rating": 5,
        "comment": "Corte super moderno, amei o caimento!",
        "date": "Set 2026"
      }
    ],
    "discountSlots": [
      {
        "time": "11:30",
        "discountPct": 25,
        "type": "economy"
      },
      {
        "time": "13:00",
        "discountPct": 30,
        "type": "economy"
      },
      {
        "time": "16:00",
        "discountPct": 25,
        "type": "urgent"
      }
    ],
    "services": [
      {
        "id": "srv_s15_1",
        "name": "Corte Moderno Bob & Nutrição Lipídica",
        "durationMinutes": 50,
        "basePrice": 135,
        "category": "hair",
        "description": "Alinhamento de fios, corte moderno e nutrição à base de óleos nobres."
      },
      {
        "id": "srv_s15_2",
        "name": "Escova Modeladora Suave",
        "durationMinutes": 40,
        "basePrice": 75,
        "category": "hair",
        "description": "Lavagem relaxante no lavatório e escovação com brilho sedoso acetinado."
      },
      {
        "id": "srv_s15_3",
        "name": "Cauterização Capilar com Queratina Hidrolisada",
        "durationMinutes": 60,
        "basePrice": 160,
        "category": "hair",
        "description": "Selamento das cutículas abertas e preenchimento de massa para cabelos com química."
      },
      {
        "id": "srv_s15_4",
        "name": "Cronograma Capilar Fase Nutrição com Óleos",
        "durationMinutes": 45,
        "basePrice": 90,
        "category": "hair",
        "description": "Nutrição lipídica profunda com óleos de macadâmia, mirra e manteiga de karité."
      },
      {
        "id": "srv_s15_5",
        "name": "Ajuste e Corte de Franja",
        "durationMinutes": 20,
        "basePrice": 40,
        "category": "hair",
        "description": "Ajuste rápido de comprimento e caimento da franja com secagem direcionada."
      }
    ],
    "leadStaff": {
      "name": "Lucas Alencar",
      "role": "Colorista Criativo & Hair Designer",
      "avatar": "https://images.unsplash.com/photo-1519085360753-af0119f7cbe7?auto=format&fit=crop&w=200&h=200&q=80",
      "verified": true
    },
    "mutualNetwork": {
      "friendsCount": 3,
      "avatars": [
        "https://images.unsplash.com/photo-1524504388940-b1c1722653e1?auto=format&fit=crop&w=100&h=100&q=80",
        "https://images.unsplash.com/photo-1517841905240-472988babdf9?auto=format&fit=crop&w=100&h=100&q=80",
        "https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&w=100&h=100&q=80"
      ],
      "text": "Larissa, Beatriz e Camila cortam o cabelo na Vila Madalena aqui"
    }
  },
  {
    "id": "s16",
    "name": "Nails & Co. Express",
    "neighborhood": "Jardins",
    "category": "nails",
    "rating": 4.74,
    "reviewsCount": 142,
    "distanceKm": 1.7,
    "lat": -23.566,
    "lng": -46.6695,
    "address": "Al. Lorena, 1380 - Jardins, São Paulo",
    "image": "https://images.unsplash.com/photo-1522337660859-02fbefca4702?auto=format&fit=crop&w=800&q=80",
    "socialProof": "#Camila fez Blindagem de Diamante há 14 min",
    "socialAvatar": "https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&w=100&h=100&q=80",
    "service": {
      "id": "srv_s16_1",
      "name": "Manicure Express & Hidratação de Mãos",
      "durationMinutes": 40,
      "basePrice": 65,
      "category": "nails",
      "description": "Esmaltação rápida com produtos importados e massagem relaxante nas mãos."
    },
    "staff": [
      {
        "id": "st21",
        "name": "Solange Prado",
        "role": "Manicure",
        "rating": 4.82,
        "avatar": "https://images.unsplash.com/photo-1580489944761-15a19d654956?auto=format&fit=crop&w=120&q=80"
      }
    ],
    "reviews": [
      {
        "author": "Beatriz H.",
        "rating": 5,
        "comment": "Prático e rápido para o dia a dia!",
        "date": "Set 2026"
      }
    ],
    "discountSlots": [
      {
        "time": "12:00",
        "discountPct": 20,
        "type": "economy"
      },
      {
        "time": "14:30",
        "discountPct": 30,
        "type": "economy"
      }
    ],
    "services": [
      {
        "id": "srv_s16_1",
        "name": "Manicure Express & Hidratação de Mãos",
        "durationMinutes": 40,
        "basePrice": 65,
        "category": "nails",
        "description": "Esmaltação rápida com produtos importados e massagem relaxante nas mãos."
      },
      {
        "id": "srv_s16_2",
        "name": "Combo Manicure e Pedicure Tradicional",
        "durationMinutes": 65,
        "basePrice": 95,
        "category": "nails",
        "description": "Cuidado completo das unhas das mãos e pés em sequência contínua e sem esperas."
      },
      {
        "id": "srv_s16_3",
        "name": "Esmaltação Rápida com Lixamento",
        "durationMinutes": 25,
        "basePrice": 38,
        "category": "nails",
        "description": "Lixamento, alinhamento de bordas e aplicação de esmalte de alta durabilidade."
      },
      {
        "id": "srv_s16_4",
        "name": "Blindagem de Queratina para Unhas Frágeis",
        "durationMinutes": 45,
        "basePrice": 70,
        "category": "nails",
        "description": "Camada de proteção acrílica em gel para evitar lascas e quebras de unhas fracas."
      },
      {
        "id": "srv_s16_5",
        "name": "Depilação de Buço e Queixo com Linha Egípcia",
        "durationMinutes": 20,
        "basePrice": 35,
        "category": "depilation",
        "description": "Epilação facial higiênica e hipoalergênica que arranca os pelos pela raiz."
      }
    ],
    "leadStaff": {
      "name": "Amanda Prado",
      "role": "Nail Designer de Alongamento Fibra",
      "avatar": "https://images.unsplash.com/photo-1494790108377-be9c29b29330?auto=format&fit=crop&w=200&h=200&q=80",
      "verified": true
    },
    "mutualNetwork": {
      "friendsCount": 2,
      "avatars": [
        "https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&w=100&h=100&q=80",
        "https://images.unsplash.com/photo-1567532939604-b6b5b0db2604?auto=format&fit=crop&w=100&h=100&q=80"
      ],
      "text": "Camila e Rafaela frequentam este espaço nos Jardins"
    }
  },
  {
    "id": "s17",
    "name": "Zen Terapia Corporal",
    "neighborhood": "Pinheiros",
    "category": "massage",
    "rating": 4.91,
    "reviewsCount": 180,
    "distanceKm": 0.9,
    "lat": -23.5615,
    "lng": -46.689,
    "address": "R. Simão Álvares, 415 - Pinheiros, São Paulo",
    "image": "https://images.unsplash.com/photo-1519823551278-64ac92734fb1?auto=format&fit=crop&w=800&q=80",
    "socialProof": "#Juliana agendou Shiatsu Integrativo há 28 min",
    "socialAvatar": "https://images.unsplash.com/photo-1544005313-94ddf0286df2?auto=format&fit=crop&w=100&h=100&q=80",
    "service": {
      "id": "srv_s17_1",
      "name": "Sessão de Shiatsu & Reflexologia Podal",
      "durationMinutes": 60,
      "basePrice": 165,
      "category": "massage",
      "description": "Pressão pontual nos meridianos energéticos e massagem revigorante nos pés."
    },
    "staff": [
      {
        "id": "st22",
        "name": "Kenji Sato",
        "role": "Mestre em Shiatsu",
        "rating": 4.97,
        "avatar": "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?auto=format&fit=crop&w=120&q=80"
      }
    ],
    "reviews": [
      {
        "author": "Rodrigo B.",
        "rating": 5,
        "comment": "Kenji é incrível. Saí sem nenhuma dor.",
        "date": "Set 2026"
      }
    ],
    "discountSlots": [
      {
        "time": "10:00",
        "discountPct": 25,
        "type": "economy"
      },
      {
        "time": "13:00",
        "discountPct": 35,
        "type": "economy"
      },
      {
        "time": "17:00",
        "discountPct": 30,
        "type": "urgent"
      }
    ],
    "services": [
      {
        "id": "srv_s17_1",
        "name": "Sessão de Shiatsu & Reflexologia Podal",
        "durationMinutes": 60,
        "basePrice": 165,
        "category": "massage",
        "description": "Pressão pontual nos meridianos energéticos e massagem revigorante nos pés."
      },
      {
        "id": "srv_s17_2",
        "name": "Massagem Relaxante com Aromaterapia Botânica",
        "durationMinutes": 50,
        "basePrice": 140,
        "category": "massage",
        "description": "Manobras lentas e envolventes com óleo morno enriquecido com bergamota e camomila."
      },
      {
        "id": "srv_s17_3",
        "name": "Reflexologia Podal Terapêutica",
        "durationMinutes": 40,
        "basePrice": 85,
        "category": "massage",
        "description": "Estimulação reflexa dos pontos correspondentes aos órgãos na sola dos pés."
      },
      {
        "id": "srv_s17_4",
        "name": "Drenagem Linfática Corporal com Desintoxicação",
        "durationMinutes": 60,
        "basePrice": 175,
        "category": "massage",
        "description": "Aceleração do retorno linfático com foco em membros inferiores e abdômen."
      },
      {
        "id": "srv_s17_5",
        "name": "Massagem Ayurvédica com Óleos Aquecidos",
        "durationMinutes": 70,
        "basePrice": 190,
        "category": "massage",
        "description": "Técnica milenar indiana que atua no sistema circulatório e descompressão articular."
      }
    ],
    "leadStaff": {
      "name": "Rodrigo Zanin",
      "role": "Fisioterapeuta & Massoterapeuta",
      "avatar": "https://images.unsplash.com/photo-1506794778202-cad84cf45f1d?auto=format&fit=crop&w=200&h=200&q=80",
      "verified": true
    },
    "mutualNetwork": {
      "friendsCount": 3,
      "avatars": [
        "https://images.unsplash.com/photo-1544005313-94ddf0286df2?auto=format&fit=crop&w=100&h=100&q=80",
        "https://images.unsplash.com/photo-1508214751196-bcfd4ca60f91?auto=format&fit=crop&w=100&h=100&q=80",
        "https://images.unsplash.com/photo-1548142813-c348350df52b?auto=format&fit=crop&w=100&h=100&q=80"
      ],
      "text": "Juliana, Bianca e Carolina fazem liberação miofascial aqui"
    }
  },
  {
    "id": "s18",
    "name": "DermoLaser Estética",
    "neighborhood": "Itaim Bibi",
    "category": "facial",
    "rating": 4.8,
    "reviewsCount": 165,
    "distanceKm": 2.6,
    "lat": -23.582,
    "lng": -46.673,
    "address": "R. Pedroso Alvarenga, 1200 - Itaim Bibi, São Paulo",
    "image": "https://images.unsplash.com/photo-1516549655169-df83a0774514?auto=format&fit=crop&w=800&q=80",
    "socialProof": "#Beatriz fez Peeling Ultrassônico há 16 min",
    "socialAvatar": "https://images.unsplash.com/photo-1517841905240-472988babdf9?auto=format&fit=crop&w=100&h=100&q=80",
    "service": {
      "id": "srv_s18_1",
      "name": "Protocolo Glow Facial & Peeling de Ácido Hialurônico",
      "durationMinutes": 60,
      "basePrice": 220,
      "category": "esthetic",
      "description": "Laser de baixa intensidade com infusão de ácido hialurônico para hidratação máxima."
    },
    "staff": [
      {
        "id": "st23",
        "name": "Dra. Gabriela Fontes",
        "role": "Dermatofuncional",
        "rating": 4.92,
        "avatar": "https://images.unsplash.com/photo-1559839734-2b71ea197ec2?auto=format&fit=crop&w=120&q=80"
      }
    ],
    "reviews": [
      {
        "author": "Ana Paula G.",
        "rating": 5,
        "comment": "Resultado sensacional já na primeira sessão.",
        "date": "Set 2026"
      }
    ],
    "discountSlots": [
      {
        "time": "11:00",
        "discountPct": 20,
        "type": "economy"
      },
      {
        "time": "14:00",
        "discountPct": 30,
        "type": "economy"
      }
    ],
    "services": [
      {
        "id": "srv_s18_1",
        "name": "Protocolo Glow Facial & Peeling de Ácido Hialurônico",
        "durationMinutes": 60,
        "basePrice": 220,
        "category": "esthetic",
        "description": "Laser de baixa intensidade com infusão de ácido hialurônico para hidratação máxima."
      },
      {
        "id": "srv_s18_2",
        "name": "Limpeza de Pele Fotônica com Luz LED Azul",
        "durationMinutes": 65,
        "basePrice": 170,
        "category": "esthetic",
        "description": "Ação bactericida contra acne associada a extração indolor de comedões."
      },
      {
        "id": "srv_s18_3",
        "name": "Drenagem Facial Pós-Procedimento",
        "durationMinutes": 45,
        "basePrice": 130,
        "category": "esthetic",
        "description": "Redução acelerada de edemas faciais e reativação da microcirculação cutânea."
      },
      {
        "id": "srv_s18_4",
        "name": "Tratamento Revitalizante de Olheiras e Pálpebras",
        "durationMinutes": 40,
        "basePrice": 110,
        "category": "esthetic",
        "description": "Microcorrentes e sérum de cafeína pura para clareamento da região periocular."
      },
      {
        "id": "srv_s18_5",
        "name": "Depilação a Laser Alexandrite Região Facial",
        "durationMinutes": 30,
        "basePrice": 120,
        "category": "depilation",
        "description": "Destruição do folículo piloso com tecnologia de resfriamento para conforto total."
      }
    ],
    "leadStaff": {
      "name": "Dra. Luciana Bicalho",
      "role": "Médica Dermatologista Estética",
      "avatar": "https://images.unsplash.com/photo-1598256989800-fe5f95da9787?auto=format&fit=crop&w=200&h=200&q=80",
      "verified": true
    },
    "mutualNetwork": {
      "friendsCount": 2,
      "avatars": [
        "https://images.unsplash.com/photo-1517841905240-472988babdf9?auto=format&fit=crop&w=100&h=100&q=80",
        "https://images.unsplash.com/photo-1494790108377-be9c29b29330?auto=format&fit=crop&w=100&h=100&q=80"
      ],
      "text": "Beatriz e Mariana recomendam os tratamentos a laser"
    }
  },
  {
    "id": "s19",
    "name": "Barba & Navalha",
    "neighborhood": "Perdizes",
    "category": "barber",
    "rating": 4.72,
    "reviewsCount": 130,
    "distanceKm": 3.1,
    "lat": -23.535,
    "lng": -46.671,
    "address": "R. Desembargador do Vale, 320 - Perdizes, São Paulo",
    "image": "https://images.unsplash.com/photo-1621605815971-fbc98d665033?auto=format&fit=crop&w=800&q=80",
    "socialProof": "#Felipe fez Corte Tesoura há 21 min",
    "socialAvatar": "https://images.unsplash.com/photo-1522075469751-3a6694fb2f61?auto=format&fit=crop&w=100&h=100&q=80",
    "service": {
      "id": "srv_s19_1",
      "name": "Navalhete Clássico & Tratamento Capilar",
      "durationMinutes": 45,
      "basePrice": 75,
      "category": "barber",
      "description": "Corte tradicional com navalhete, massagem capilar e loção pós-barba refrescante."
    },
    "staff": [
      {
        "id": "st24",
        "name": "Diego Ramos",
        "role": "Barbeiro Chefe",
        "rating": 4.84,
        "avatar": "https://images.unsplash.com/photo-1506794778202-cad84cf45f1d?auto=format&fit=crop&w=120&q=80"
      }
    ],
    "reviews": [
      {
        "author": "Leonardo C.",
        "rating": 5,
        "comment": "Lugar nota 10, atendimento pontual e cerveja cortesia.",
        "date": "Set 2026"
      }
    ],
    "discountSlots": [
      {
        "time": "09:30",
        "discountPct": 25,
        "type": "economy"
      },
      {
        "time": "13:30",
        "discountPct": 30,
        "type": "economy"
      }
    ],
    "services": [
      {
        "id": "srv_s19_1",
        "name": "Navalhete Clássico & Tratamento Capilar",
        "durationMinutes": 45,
        "basePrice": 75,
        "category": "barber",
        "description": "Corte tradicional com navalhete, massagem capilar e loção pós-barba refrescante."
      },
      {
        "id": "srv_s19_2",
        "name": "Corte Masculino Degradê Navalhado",
        "durationMinutes": 35,
        "basePrice": 50,
        "category": "barber",
        "description": "Fade de alta precisão com acabamento milimétrico e penteado modelado."
      },
      {
        "id": "srv_s19_3",
        "name": "Barba Esculpida com Esfoliação Térmica",
        "durationMinutes": 30,
        "basePrice": 40,
        "category": "barber",
        "description": "Linhas desenhadas com precisão e esfoliação contra foliculite no pescoço."
      },
      {
        "id": "srv_s19_4",
        "name": "Descoloração e Platinado Global",
        "durationMinutes": 90,
        "basePrice": 180,
        "category": "barber",
        "description": "Abertura de tom uniforme com proteção dos fios e neutralização fria platinada."
      },
      {
        "id": "srv_s19_5",
        "name": "Lavagem Mentolada & Massagem Craniana",
        "durationMinutes": 20,
        "basePrice": 30,
        "category": "barber",
        "description": "Shampoo mentolado adstringente com massagem nos pontos de alívio craniano."
      }
    ],
    "leadStaff": {
      "name": "Alexandre Fonseca",
      "role": "Barber Designer & Barboterapeuta",
      "avatar": "https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?auto=format&fit=crop&w=200&h=200&q=80",
      "verified": true
    },
    "mutualNetwork": {
      "friendsCount": 3,
      "avatars": [
        "https://images.unsplash.com/photo-1522075469751-3a6694fb2f61?auto=format&fit=crop&w=100&h=100&q=80",
        "https://images.unsplash.com/photo-1506794778202-cad84cf45f1d?auto=format&fit=crop&w=100&h=100&q=80",
        "https://images.unsplash.com/photo-1492562080023-ab3db95bfbce?auto=format&fit=crop&w=100&h=100&q=80"
      ],
      "text": "Espaço muito bem avaliado por conhecidos em Perdizes"
    }
  },
  {
    "id": "s20",
    "name": "Studio Blondie & More",
    "neighborhood": "Consolação",
    "category": "hair",
    "rating": 4.68,
    "reviewsCount": 110,
    "distanceKm": 2.7,
    "lat": -23.553,
    "lng": -46.657,
    "address": "R. Bela Cintra, 890 - Consolação, São Paulo",
    "image": "https://images.unsplash.com/photo-1470259078437-5e97af7720b1?auto=format&fit=crop&w=800&q=80",
    "socialProof": "#Sofia agendou Iluminação & Brilho há 13 min",
    "socialAvatar": "https://images.unsplash.com/photo-1529626455594-4ff0802cfb7e?auto=format&fit=crop&w=100&h=100&q=80",
    "service": {
      "id": "srv_s20_1",
      "name": "Tonalização Express & Escova Modeladora",
      "durationMinutes": 60,
      "basePrice": 145,
      "category": "hair",
      "description": "Banho de brilho para iluminar mechas e escova polida com óleo de argan."
    },
    "staff": [
      {
        "id": "st25",
        "name": "Carla Nogueira",
        "role": "Colorista",
        "rating": 4.77,
        "avatar": "https://images.unsplash.com/photo-1567532939604-b6b5b0db2604?auto=format&fit=crop&w=120&q=80"
      }
    ],
    "reviews": [
      {
        "author": "Jessica R.",
        "rating": 4,
        "comment": "Meu loiro ficou renovado e sem frizz.",
        "date": "Set 2026"
      }
    ],
    "discountSlots": [
      {
        "time": "10:00",
        "discountPct": 20,
        "type": "economy"
      },
      {
        "time": "14:00",
        "discountPct": 35,
        "type": "economy"
      }
    ],
    "services": [
      {
        "id": "srv_s20_1",
        "name": "Tonalização Express & Escova Modeladora",
        "durationMinutes": 60,
        "basePrice": 145,
        "category": "hair",
        "description": "Banho de brilho para iluminar mechas e escova polida com óleo de argan."
      },
      {
        "id": "srv_s20_2",
        "name": "Corte Feminino com Texturização Suave",
        "durationMinutes": 45,
        "basePrice": 110,
        "category": "hair",
        "description": "Corte que confere balanço natural e leveza para cabelos finos ou volumosos."
      },
      {
        "id": "srv_s20_3",
        "name": "Reconstrução Joico K-Pak 4 Passos",
        "durationMinutes": 50,
        "basePrice": 150,
        "category": "hair",
        "description": "Reposição de aminoácidos estruturais para fios elásticos e quebradiços."
      },
      {
        "id": "srv_s20_4",
        "name": "Escova Modeladora Glamour Ondulada",
        "durationMinutes": 40,
        "basePrice": 70,
        "category": "hair",
        "description": "Finalização impecável para eventos com volume na raiz e pontas modeladas."
      },
      {
        "id": "srv_s20_5",
        "name": "Design de Sobrancelhas na Pinça com Visagismo",
        "durationMinutes": 30,
        "basePrice": 45,
        "category": "esthetic",
        "description": "Harmonização do formato das sobrancelhas respeitando a simetria facial."
      }
    ],
    "leadStaff": {
      "name": "Isabela Ferraz",
      "role": "Blond Specialist & Terapeuta Capilar",
      "avatar": "https://images.unsplash.com/photo-1531746020798-e6953c6e8e04?auto=format&fit=crop&w=200&h=200&q=80",
      "verified": true
    },
    "mutualNetwork": {
      "friendsCount": 3,
      "avatars": [
        "https://images.unsplash.com/photo-1529626455594-4ff0802cfb7e?auto=format&fit=crop&w=100&h=100&q=80",
        "https://images.unsplash.com/photo-1567532939604-b6b5b0db2604?auto=format&fit=crop&w=100&h=100&q=80",
        "https://images.unsplash.com/photo-1524504388940-b1c1722653e1?auto=format&fit=crop&w=100&h=100&q=80"
      ],
      "text": "Sofia, Rafaela e Larissa cuidam do loiro aqui"
    }
  },
  {
    "id": "s21",
    "name": "Bella Donna Nail Bar",
    "neighborhood": "Vila Madalena",
    "category": "nails",
    "rating": 4.79,
    "reviewsCount": 140,
    "distanceKm": 2.4,
    "lat": -23.5585,
    "lng": -46.697,
    "address": "R. Fradique Coutinho, 1380 - Vila Madalena, São Paulo",
    "image": "https://images.unsplash.com/photo-1596462502278-27bfdc403348?auto=format&fit=crop&w=800&q=80",
    "socialProof": "#Mariana agendou Spa dos Pés há 35 min",
    "socialAvatar": "https://images.unsplash.com/photo-1494790108377-be9c29b29330?auto=format&fit=crop&w=100&h=100&q=80",
    "service": {
      "id": "srv_s21_1",
      "name": "Spa dos Pés com Esfoliação & Parafina Morna",
      "durationMinutes": 50,
      "basePrice": 85,
      "category": "nails",
      "description": "Hidratação profunda para pés ressecados com esfoliação botânica e cera de parafina."
    },
    "staff": [
      {
        "id": "st26",
        "name": "Lucia Barros",
        "role": "Podóloga & Nail Designer",
        "rating": 4.88,
        "avatar": "https://images.unsplash.com/photo-1573496359142-b8d87734a5a2?auto=format&fit=crop&w=120&q=80"
      }
    ],
    "reviews": [
      {
        "author": "Camila S.",
        "rating": 5,
        "comment": "Pés macios como de bebê! Atendimento nota 1000.",
        "date": "Set 2026"
      }
    ],
    "discountSlots": [
      {
        "time": "11:00",
        "discountPct": 30,
        "type": "economy"
      },
      {
        "time": "15:30",
        "discountPct": 30,
        "type": "economy"
      }
    ],
    "services": [
      {
        "id": "srv_s21_1",
        "name": "Spa dos Pés com Esfoliação & Parafina Morna",
        "durationMinutes": 50,
        "basePrice": 85,
        "category": "nails",
        "description": "Hidratação profunda para pés ressecados com esfoliação botânica e cera de parafina."
      },
      {
        "id": "srv_s21_2",
        "name": "Combo Manicure e Pedicure Express",
        "durationMinutes": 50,
        "basePrice": 75,
        "category": "nails",
        "description": "Atendimento ágil para mãos e pés com cuticulagem e esmaltação perfeita."
      },
      {
        "id": "srv_s21_3",
        "name": "Esmaltação em Gel nas Mãos",
        "durationMinutes": 45,
        "basePrice": 65,
        "category": "nails",
        "description": "Cor vibrante e película de gel resistente que não descasca ao lavar louça ou digitar."
      },
      {
        "id": "srv_s21_4",
        "name": "Alongamento Polygel com Acabamento Natural",
        "durationMinutes": 80,
        "basePrice": 160,
        "category": "nails",
        "description": "Híbrido de pó acrílico e gel de cura rápida com curvatura C anatômica."
      },
      {
        "id": "srv_s21_5",
        "name": "Depilação Meia Perna com Cera Hidrossolúvel de Mel",
        "durationMinutes": 30,
        "basePrice": 45,
        "category": "depilation",
        "description": "Epilação suave com resíduo removível em água para menor sensibilidade na pele."
      }
    ],
    "leadStaff": {
      "name": "Natália Castro",
      "role": "Nail Artist & Spa Podal",
      "avatar": "https://images.unsplash.com/photo-1548142813-c348350df52b?auto=format&fit=crop&w=200&h=200&q=80",
      "verified": true
    },
    "mutualNetwork": {
      "friendsCount": 2,
      "avatars": [
        "https://images.unsplash.com/photo-1494790108377-be9c29b29330?auto=format&fit=crop&w=100&h=100&q=80",
        "https://images.unsplash.com/photo-1544005313-94ddf0286df2?auto=format&fit=crop&w=100&h=100&q=80"
      ],
      "text": "Mariana e Juliana frequentam este espaço no Itaim"
    }
  },
  {
    "id": "s22",
    "name": "Equilibrium Spa Urbano",
    "neighborhood": "Jardins",
    "category": "massage",
    "rating": 4.89,
    "reviewsCount": 245,
    "distanceKm": 1.6,
    "lat": -23.567,
    "lng": -46.67,
    "address": "R. Haddock Lobo, 950 - Cerqueira César, São Paulo",
    "image": "https://images.unsplash.com/photo-1506126613408-eca07ce68773?auto=format&fit=crop&w=800&q=80",
    "socialProof": "#Carolina agendou Drenagem Linfática há 9 min",
    "socialAvatar": "https://images.unsplash.com/photo-1548142813-c348350df52b?auto=format&fit=crop&w=100&h=100&q=80",
    "service": {
      "id": "srv_s22_1",
      "name": "Drenagem Linfática Corporal Método Renata França",
      "durationMinutes": 60,
      "basePrice": 210,
      "category": "massage",
      "description": "Manobras manuais precisas que reduzem edemas e remodelam as curvas imediatamente."
    },
    "staff": [
      {
        "id": "st27",
        "name": "Flavia Mendonça",
        "role": "Especialista em Drenagem",
        "rating": 4.95,
        "avatar": "https://images.unsplash.com/photo-1580489944761-15a19d654956?auto=format&fit=crop&w=120&q=80"
      }
    ],
    "reviews": [
      {
        "author": "Leticia O.",
        "rating": 5,
        "comment": "Resultado visível na hora! Desinchei horrores.",
        "date": "Set 2026"
      }
    ],
    "discountSlots": [
      {
        "time": "10:30",
        "discountPct": 25,
        "type": "economy"
      },
      {
        "time": "14:00",
        "discountPct": 30,
        "type": "economy"
      },
      {
        "time": "17:00",
        "discountPct": 20,
        "type": "urgent"
      }
    ],
    "services": [
      {
        "id": "srv_s22_1",
        "name": "Drenagem Linfática Corporal Método Renata França",
        "durationMinutes": 60,
        "basePrice": 210,
        "category": "massage",
        "description": "Manobras manuais precisas que reduzem edemas e remodelam as curvas imediatamente."
      },
      {
        "id": "srv_s22_2",
        "name": "Massagem Relaxante com Óleo Puro de Amêndoas",
        "durationMinutes": 50,
        "basePrice": 155,
        "category": "massage",
        "description": "Descompressão muscular suave para aliviar o cansaço do dia a dia."
      },
      {
        "id": "srv_s22_3",
        "name": "Massagem Modeladora Redutora Turbinada",
        "durationMinutes": 50,
        "basePrice": 165,
        "category": "massage",
        "description": "Manobras vigorosas e profundas com foco em gordura localizada e celulite."
      },
      {
        "id": "srv_s22_4",
        "name": "Massagem a Quatro Mãos em Perfeita Sincronia",
        "durationMinutes": 50,
        "basePrice": 270,
        "category": "massage",
        "description": "Duas terapeutas trabalhando em ritmo unificado para desconexão sensorial absoluta."
      },
      {
        "id": "srv_s22_5",
        "name": "Esfoliação Corporal com Argila Verde e Chá Verde",
        "durationMinutes": 45,
        "basePrice": 130,
        "category": "esthetic",
        "description": "Remoção de células mortas e remineralização dérmica para toque de seda."
      }
    ],
    "leadStaff": {
      "name": "Helena Rangel",
      "role": "Especialista em Drenagem Integrativa",
      "avatar": "https://images.unsplash.com/photo-1529626455594-4ff0802cfb7e?auto=format&fit=crop&w=200&h=200&q=80",
      "verified": true
    },
    "mutualNetwork": {
      "friendsCount": 3,
      "avatars": [
        "https://images.unsplash.com/photo-1548142813-c348350df52b?auto=format&fit=crop&w=100&h=100&q=80",
        "https://images.unsplash.com/photo-1508214751196-bcfd4ca60f91?auto=format&fit=crop&w=100&h=100&q=80",
        "https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&w=100&h=100&q=80"
      ],
      "text": "Carolina, Bianca e Camila elogiam o atendimento acolhedor"
    }
  },
  {
    "id": "s23",
    "name": "Face & Care Concept",
    "neighborhood": "Pinheiros",
    "category": "facial",
    "rating": 4.86,
    "reviewsCount": 155,
    "distanceKm": 1.3,
    "lat": -23.5655,
    "lng": -46.6845,
    "address": "R. Artur de Azevedo, 780 - Pinheiros, São Paulo",
    "image": "https://images.unsplash.com/photo-1515377905703-c4788e51af15?auto=format&fit=crop&w=800&q=80",
    "socialProof": "#Gabriela agendou Hidragloss Lips há 27 min",
    "socialAvatar": "https://images.unsplash.com/photo-1531746020798-e6953c6e8e04?auto=format&fit=crop&w=100&h=100&q=80",
    "service": {
      "id": "srv_s23_1",
      "name": "Fototerapia LED & Hidratação de Colágeno",
      "durationMinutes": 50,
      "basePrice": 160,
      "category": "esthetic",
      "description": "Luz emitida por diodos para regeneração celular e máscara oclusiva de colágeno marinho."
    },
    "staff": [
      {
        "id": "st28",
        "name": "Dr. Bruno Silveira",
        "role": "Dermatologista",
        "rating": 4.91,
        "avatar": "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?auto=format&fit=crop&w=120&q=80"
      }
    ],
    "reviews": [
      {
        "author": "Carolina D.",
        "rating": 5,
        "comment": "Clínica linda e procedimento super relaxante.",
        "date": "Set 2026"
      }
    ],
    "discountSlots": [
      {
        "time": "11:00",
        "discountPct": 30,
        "type": "economy"
      },
      {
        "time": "15:00",
        "discountPct": 35,
        "type": "economy"
      }
    ],
    "services": [
      {
        "id": "srv_s23_1",
        "name": "Fototerapia LED & Hidratação de Colágeno",
        "durationMinutes": 50,
        "basePrice": 160,
        "category": "esthetic",
        "description": "Luz emitida por diodos para regeneração celular e máscara oclusiva de colágeno marinho."
      },
      {
        "id": "srv_s23_2",
        "name": "Limpeza de Pele Detox com Máscara de Carvão Ativado",
        "durationMinutes": 60,
        "basePrice": 145,
        "category": "esthetic",
        "description": "Purificação dos poros e remoção de poluição urbana com propriedades adstringentes."
      },
      {
        "id": "srv_s23_3",
        "name": "Peeling Enzimático Suave de Romã e Mamão",
        "durationMinutes": 45,
        "basePrice": 135,
        "category": "esthetic",
        "description": "Renovação cutânea biológica sem irritação ou descamações agressivas."
      },
      {
        "id": "srv_s23_4",
        "name": "Lifting Facial com Microcorrentes Dermoestimulantes",
        "durationMinutes": 45,
        "basePrice": 180,
        "category": "esthetic",
        "description": "Eletroestimulação de baixa intensidade para reeducação muscular facial e viço."
      },
      {
        "id": "srv_s23_5",
        "name": "Depilação de Buço e Sobrancelha com Cera Calmante",
        "durationMinutes": 25,
        "basePrice": 45,
        "category": "depilation",
        "description": "Fórmula enriquecida com óleo de camomila para evitar vermelhidão na pele sensível."
      }
    ],
    "leadStaff": {
      "name": "Dra. Gabriela Vasconcelos",
      "role": "Cirurgiã Dentista & Harmonização Facial",
      "avatar": "https://images.unsplash.com/photo-1580894732444-8ecded7900cd?auto=format&fit=crop&w=200&h=200&q=80",
      "verified": true
    },
    "mutualNetwork": {
      "friendsCount": 2,
      "avatars": [
        "https://images.unsplash.com/photo-1531746020798-e6953c6e8e04?auto=format&fit=crop&w=100&h=100&q=80",
        "https://images.unsplash.com/photo-1573497019940-1c28c88b4f3e?auto=format&fit=crop&w=100&h=100&q=80"
      ],
      "text": "Gabriela e Danielle realizam cuidados faciais com a Dra. Gabriela"
    }
  },
  {
    "id": "s24",
    "name": "Blend Beleza & Estilo",
    "neighborhood": "Itaim Bibi",
    "category": "mixed",
    "rating": 4.77,
    "reviewsCount": 178,
    "distanceKm": 2.2,
    "lat": -23.5795,
    "lng": -46.675,
    "address": "R. Tabapuã, 620 - Itaim Bibi, São Paulo",
    "image": "https://images.unsplash.com/photo-1527799820374-dcf8d9d4a388?auto=format&fit=crop&w=800&q=80",
    "socialProof": "#Paula agendou Corte Moderno há 18 min",
    "socialAvatar": "https://images.unsplash.com/photo-1573496359142-b8d87734a5a2?auto=format&fit=crop&w=100&h=100&q=80",
    "service": {
      "id": "srv_s24_1",
      "name": "Combo Corte Unissex & Escova Polida",
      "durationMinutes": 55,
      "basePrice": 110,
      "category": "hair",
      "description": "Atendimento unissex prático com lavagem refrescante e modelagem rápida."
    },
    "staff": [
      {
        "id": "st29",
        "name": "Andre Vianna",
        "role": "Master Hair Stylist",
        "rating": 4.86,
        "avatar": "https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?auto=format&fit=crop&w=120&q=80"
      }
    ],
    "reviews": [
      {
        "author": "Renato S.",
        "rating": 5,
        "comment": "Sempre salvo meu visual com o Andre. Rápido e perfeito.",
        "date": "Set 2026"
      }
    ],
    "discountSlots": [
      {
        "time": "10:00",
        "discountPct": 20,
        "type": "economy"
      },
      {
        "time": "13:00",
        "discountPct": 30,
        "type": "economy"
      },
      {
        "time": "16:30",
        "discountPct": 25,
        "type": "urgent"
      }
    ],
    "services": [
      {
        "id": "srv_s24_1",
        "name": "Combo Corte Unissex & Escova Polida",
        "durationMinutes": 55,
        "basePrice": 110,
        "category": "hair",
        "description": "Atendimento unissex prático com lavagem refrescante e modelagem rápida."
      },
      {
        "id": "srv_s24_2",
        "name": "Barba Completa na Navalha & Penteado Masculino",
        "durationMinutes": 45,
        "basePrice": 65,
        "category": "barber",
        "description": "Alinhamento visual completo para compromissos e reuniões de trabalho."
      },
      {
        "id": "srv_s24_3",
        "name": "Manicure Express Unissex de Higienização",
        "durationMinutes": 30,
        "basePrice": 38,
        "category": "nails",
        "description": "Unhas limpas, lixadas e com polimento fosco ou base incolor protetora."
      },
      {
        "id": "srv_s24_4",
        "name": "Massagem Craniofacial no Lavatório",
        "durationMinutes": 20,
        "basePrice": 35,
        "category": "massage",
        "description": "Descompressão da nuca e têmporas com loção aromática durante a lavagem."
      },
      {
        "id": "srv_s24_5",
        "name": "Escova Progressiva Orgânica Sem Formol",
        "durationMinutes": 90,
        "basePrice": 230,
        "category": "hair",
        "description": "Redução duradoura de volume com ativos botânicos e brilho intenso espelhado."
      }
    ],
    "leadStaff": {
      "name": "Felipe Morais",
      "role": "Hair Director & Expert em Texturas",
      "avatar": "https://images.unsplash.com/photo-1522075469751-3a6694fb2f61?auto=format&fit=crop&w=200&h=200&q=80",
      "verified": true
    },
    "mutualNetwork": {
      "friendsCount": 3,
      "avatars": [
        "https://images.unsplash.com/photo-1573496359142-b8d87734a5a2?auto=format&fit=crop&w=100&h=100&q=80",
        "https://images.unsplash.com/photo-1494790108377-be9c29b29330?auto=format&fit=crop&w=100&h=100&q=80",
        "https://images.unsplash.com/photo-1580489944761-15a19d654956?auto=format&fit=crop&w=100&h=100&q=80"
      ],
      "text": "Paula, Mariana e Beatriz frequentam este lounge"
    }
  }
];

// Horários do dia (de 09:00 até 19:00 em passos de 15 min)
const TIME_SLOTS = [];
for (let h = 9; h <= 18; h++) {
  for (let m of [0, 15, 30, 45]) {
    const hh = String(h).padStart(2, '0');
    const mm = String(m).padStart(2, '0');
    TIME_SLOTS.push(`${hh}:${mm}`);
  }
}
TIME_SLOTS.push('19:00');

// ===================================================================
// 2. ESTADO GLOBAL DA APLICAÇÃO
// ===================================================================
const AppState = {
  currentScreen: 'home',
  userSession: null, // { name, phone, code, acceptedAt, sessionId }
  selectedSalon: MOCK_SALONS[0],
  selectedService: MOCK_SALONS[0].services ? MOCK_SALONS[0].services[0] : MOCK_SALONS[0].service,
  selectedStaff: { id: 'any', name: 'Qualquer Profissional', role: 'Primeiro disponível', isAny: true },
  selectedDate: '2026-09-26',
  selectedTimeSlotIndex: 20, // default ~14:00
  selectedTime: '14:00',
  currentPricing: null,
  reservationTimerSeconds: 600, // 10 min
  timerIntervalId: null,
  activeFilter: 'all',
  activeCategory: 'all',
  homeSearchQuery: '',
  isCardValid: false,
  selectedPaymentMethod: 'card', // 'card' | 'pix'
  confirmedAppointment: null,
  analyticsEvents: [],
  // Smart Match Engine State
  smartMatchWindow: 'now_2h',
  smartSelectedServiceType: 'hair_cut_fem',
  smartSelectedRadius: 3,
  smartMatchOptions: [],
  selectedMatchOption: null,
  // On-Demand Uber Flow State
  selectedDemandCategory: 'hair',
  selectedDemandWindow: 'now_2h',
  uberMapInstance: null,
  uberRoutePolyline: null,
  uberWalkingBadgeMarker: null,
  uberMarkersList: [],
  selectedDemandMatch: null,
  uberMatchesList: [],
  lastWalkTimeMin: null,
  lastWalkDistanceM: null,
  appointmentsTab: 'active',
  // Lista de Agendamentos (com seeds da Seção 12.10)
  appointmentsList: [
    {
      id: 'BP-108420',
      salon: MOCK_SALONS[0],
      service: MOCK_SALONS[0].service,
      staff: MOCK_SALONS[0].staff[0],
      time: '14:00',
      date: 'Hoje, 26 de Set',
      pricing: { finalPrice: 84.00, basePrice: 120.00, discountPct: 30, hasDiscount: true },
      status: 'CONFIRMED',
      bookedAt: new Date(Date.now() - 3600000).toISOString()
    },
    {
      id: 'BP-952130',
      salon: MOCK_SALONS[4], // Barbearia Maestro
      service: MOCK_SALONS[4].service,
      staff: MOCK_SALONS[4].staff[0],
      time: '11:00',
      date: '20 de Setembro',
      pricing: { finalPrice: 52.50, basePrice: 70.00, discountPct: 25, hasDiscount: true },
      status: 'COMPLETED',
      bookedAt: new Date(Date.now() - 86400000 * 6).toISOString()
    },
    {
      id: 'BP-884192',
      salon: MOCK_SALONS[1], // Lumina Studio
      service: MOCK_SALONS[1].service,
      staff: MOCK_SALONS[1].staff[0],
      time: '16:00',
      date: '15 de Setembro',
      pricing: { finalPrice: 68.00, basePrice: 85.00, discountPct: 20, hasDiscount: true },
      status: 'CANCELLED_BY_MERCHANT', // Requisito Seção 12.10 da spec
      cancellationReason: 'Manutenção emergencial na rede elétrica do estabelecimento',
      bookedAt: new Date(Date.now() - 86400000 * 11).toISOString()
    }
  ]
};

// ===================================================================
// 3. TOAST E INSTRUMENTAÇÃO DE EVENTOS (Seção 11 da Spec)
// ===================================================================
function showToast(message, duration = 3000) {
  const toast = document.getElementById('app-toast');
  const msgEl = document.getElementById('toast-message');
  if (!toast || !msgEl) return;
  msgEl.textContent = message;
  toast.classList.add('show');
  clearTimeout(toast._timeoutId);
  toast._timeoutId = setTimeout(() => {
    toast.classList.remove('show');
  }, duration);
}

function trackEvent(eventName, props = {}) {
  const eventPayload = {
    id: 'evt_' + Math.random().toString(36).substring(2, 9),
    eventName,
    props,
    clientTs: new Date().toISOString(),
    sessionUser: AppState.userSession ? AppState.userSession.name : 'Participante Anônimo'
  };
  AppState.analyticsEvents.push(eventPayload);

  // Persistência local dos eventos de validação
  try {
    const saved = JSON.parse(localStorage.getItem('bp_analytics_events') || '[]');
    saved.push(eventPayload);
    localStorage.setItem('bp_analytics_events', JSON.stringify(saved));
  } catch (e) {
    console.warn('Não foi possível persistir evento no storage:', e);
  }

  console.log(`%c[ANALYTICS EVENT] ${eventName}`, 'color: #0D9488; font-weight: bold;', props);
}

// ===================================================================
// 4. ONBOARDING & CONFORMIDADE LGPD (Seção 13 da Spec)
// ===================================================================
function initSessionState() {
  const savedSession = localStorage.getItem('bp_user_session');
  if (savedSession) {
    try {
      AppState.userSession = JSON.parse(savedSession);
      updateUserUI();
      navigateTo('home');
      return;
    } catch (e) {
      console.warn('Erro ao restaurar sessão:', e);
    }
  }
  // Se não houver sessão ativa, vai para o Onboarding
  navigateTo('onboarding');
}

function submitOnboarding() {
  const checkEl = document.getElementById('onboarding-terms-check');
  const termsChecked = checkEl ? checkEl.checked : false;
  const name = (document.getElementById('onboarding-name')?.value || '').trim() || 'Participante';
  const phone = (document.getElementById('onboarding-phone')?.value || '').trim() || '(11) 98765-4321';
  const code = (document.getElementById('onboarding-code')?.value || '').trim();

  if (!termsChecked) {
    showToast('É obrigatório aceitar o Termo de Privacidade para continuar.');
    return;
  }

  if (code !== '0000') {
    showToast('Código de acesso inválido. Utilize o código de teste: 0000');
    return;
  }

  const session = {
    name,
    phone,
    code,
    acceptedAt: new Date().toISOString(),
    sessionId: 'sess_' + Math.random().toString(36).substring(2, 9)
  };

  AppState.userSession = session;
  localStorage.setItem('bp_user_session', JSON.stringify(session));
  updateUserUI();

  trackEvent('app_opened', { view: 'onboarding_completed', participant: name });
  trackEvent('location_permission', { granted: true });
  showToast(`Sessão iniciada! Olá, ${name.split(' ')[0]}.`);
  navigateTo('home');
}

function updateUserUI() {
  if (!AppState.userSession) return;
  const firstName = AppState.userSession.name.split(' ')[0];
  const homeUserName = document.getElementById('home-user-name');
  if (homeUserName) homeUserName.textContent = firstName;

  const profileName = document.getElementById('profile-display-name');
  if (profileName) profileName.textContent = AppState.userSession.name;

  const profilePhone = document.getElementById('profile-display-phone');
  if (profilePhone) profilePhone.textContent = AppState.userSession.phone;

  const avatarLetter = document.getElementById('profile-avatar-letter');
  if (avatarLetter) {
    const initials = AppState.userSession.name.split(' ').filter(Boolean).map(n => n[0]).join('').substring(0, 2).toUpperCase();
    avatarLetter.textContent = initials || 'BP';
  }

  const termsDate = document.getElementById('profile-terms-date');
  if (termsDate && AppState.userSession.acceptedAt) {
    const d = new Date(AppState.userSession.acceptedAt);
    termsDate.textContent = d.toLocaleDateString('pt-BR') + ' às ' + d.toLocaleTimeString('pt-BR', { hour: '2-digit', minute: '2-digit' });
  }

  const checkoutHolder = document.getElementById('checkout-holder-name');
  if (checkoutHolder) checkoutHolder.value = AppState.userSession.name;
}

// ===================================================================
// 5. MOTOR DE PRECIFICAÇÃO DINÂMICA & DISPONIBILIDADE REALISTA (Seção 9 e 7 da Spec)
// ===================================================================

function getDayOfWeek(dateStr) {
  if (!dateStr) return 5;
  const parts = dateStr.split('-');
  if (parts.length === 3) {
    const d = new Date(Number(parts[0]), Number(parts[1]) - 1, Number(parts[2]));
    return d.getDay(); // 0 = Domingo, 1 = Segunda ... 6 = Sábado
  }
  return 5;
}

function getSalonPromoRules(salon, dateStr) {
  const dow = getDayOfWeek(dateStr);

  // Domingo (0): Salões fechados ou sem descontos
  if (dow === 0) return [];

  // Sábado (6): Sábado de manhã NENHUM desconto (alta procura absoluta).
  // Apenas 1 a 2 slots de "Última Hora" (urgente) no final da tarde (16:30, 17:30).
  if (dow === 6) {
    return [
      { time: '16:30', discountPct: 20, type: 'urgent' },
      { time: '17:30', discountPct: 20, type: 'urgent' }
    ];
  }

  // Quinta e Sexta (4 e 5): Descontos moderados (15% a 25%) em horários ociosos.
  // Quinta à noite e Sexta à tarde/noite NÃO têm desconto.
  if (dow === 4 || dow === 5) {
    return [
      { time: '10:30', discountPct: 20, type: 'economy' },
      { time: '13:30', discountPct: 25, type: 'economy' },
      { time: '14:00', discountPct: 30, type: 'economy' },
      { time: '14:30', discountPct: 25, type: 'economy' },
      { time: '15:00', discountPct: 20, type: 'economy' }
    ];
  }

  // Segunda a Quarta (1, 2, 3): Descontos fortes (25% a 35%) em horários ociosos.
  return [
    { time: '10:00', discountPct: 25, type: 'economy' },
    { time: '10:30', discountPct: 25, type: 'economy' },
    { time: '11:00', discountPct: 20, type: 'economy' },
    { time: '13:00', discountPct: 35, type: 'economy' },
    { time: '13:30', discountPct: 30, type: 'economy' },
    { time: '14:00', discountPct: 30, type: 'economy' },
    { time: '14:30', discountPct: 30, type: 'economy' },
    { time: '15:00', discountPct: 25, type: 'economy' },
    { time: '15:30', discountPct: 20, type: 'economy' }
  ];
}

function getOccupiedSlots(salon, dateStr) {
  const dow = getDayOfWeek(dateStr);

  // Domingo: Salão fechado (todos indisponíveis)
  if (dow === 0) {
    return [...TIME_SLOTS];
  }

  // Sábado: Alta ocupação geral (60-80% dos slots ocupados)
  if (dow === 6) {
    return [
      '09:00', '09:30', '10:00', '10:30', '11:00', '11:30', '12:00', '12:30',
      '13:00', '13:30', '14:30', '15:00', '15:30', '17:00', '18:00', '18:30'
    ];
  }

  // Dias de semana (1 a 5): Picos ocupados (almoço e fim de tarde)
  return ['11:30', '12:00', '12:30', '17:00', '17:30', '18:00', '18:30'];
}

function calculateSlotPrice(salon, time, service = null, dateStr = null) {
  const activeService = service || 
    (AppState.selectedSalon && AppState.selectedSalon.id === salon.id ? AppState.selectedService : null) || 
    (salon.services && salon.services[0]) || 
    salon.service;
  const basePrice = activeService ? activeService.basePrice : 100.00;
  
  const targetDate = dateStr || AppState.selectedDate;
  const promoRules = getSalonPromoRules(salon, targetDate);
  const promoRule = promoRules.find(d => d.time === time);
  const occupiedList = getOccupiedSlots(salon, targetDate);
  const isOccupied = occupiedList.includes(time);

  if (isOccupied) {
    return {
      hasDiscount: false,
      isOccupied: true,
      type: 'occupied',
      discountPct: 0,
      basePrice: basePrice,
      finalPrice: basePrice,
      badgeText: 'Horário Ocupado',
      subtext: 'slot já preenchido por outro cliente'
    };
  }

  if (promoRule) {
    const discountPct = promoRule.discountPct;
    const discountFactor = (100 - discountPct) / 100;
    const finalPrice = Math.max(25.00, Math.round(basePrice * discountFactor * 100) / 100);
    return {
      hasDiscount: true,
      isOccupied: false,
      type: promoRule.type, // 'economy' | 'urgent'
      discountPct: discountPct,
      basePrice: basePrice,
      finalPrice: finalPrice,
      badgeText: promoRule.type === 'urgent' ? 'Última Hora' : 'Horário Econômico',
      subtext: promoRule.type === 'urgent' ? 'desconto de última hora' : 'menor preço em horário ocioso'
    };
  }

  // Preço Cheio (sem desconto)
  return {
    hasDiscount: false,
    isOccupied: false,
    type: 'normal',
    discountPct: 0,
    basePrice: basePrice,
    finalPrice: basePrice,
    badgeText: null,
    subtext: 'horário de alta procura (preço integral)'
  };
}

// ===================================================================
// 6. NAVEGAÇÃO E MÁQUINA DE ESTADOS
// ===================================================================

function backFromCheckout() {
  trackEvent('checkout_abandoned', { step: 'voluntary_exit', reason: 'user_navigated_back' });
  if (AppState.timerIntervalId) {
    clearInterval(AppState.timerIntervalId);
    AppState.timerIntervalId = null;
  }
  navigateTo('detail');
}

function navigateTo(screenId) {
  // Se estiver saindo da tela de checkout sem confirmar, registra abandono voluntário
  if (AppState.currentScreen === 'checkout' && screenId !== 'checkout' && screenId !== 'confirm') {
    trackEvent('checkout_abandoned', { step: 'voluntary_exit', reason: `navigated_to_${screenId}` });
    if (AppState.timerIntervalId) {
      clearInterval(AppState.timerIntervalId);
      AppState.timerIntervalId = null;
    }
  }

  AppState.currentScreen = screenId;

  document.querySelectorAll('.screen').forEach(el => el.classList.remove('active'));
  const target = document.getElementById(`screen-${screenId}`);
  if (target) {
    target.classList.add('active');
    const viewport = document.querySelector('.app-viewport');
    if (viewport) viewport.scrollTop = 0;
  }

  // Se estiver no onboarding, oculta o bottom nav
  const bottomNav = document.getElementById('app-bottom-nav');
  if (bottomNav) {
    if (screenId === 'onboarding') {
      bottomNav.classList.add('hidden');
    } else {
      bottomNav.classList.remove('hidden');
    }
  }

  // Atualiza botões ativos da barra de navegação
  document.querySelectorAll('.nav-item').forEach(btn => {
    btn.classList.toggle('active', btn.dataset.screen === screenId);
  });
  const demandNavBtn = document.getElementById('nav-btn-demand');
  if (demandNavBtn) {
    demandNavBtn.classList.toggle('active', screenId === 'demand-service' || screenId === 'uber-demand');
  }

  // Gatilhos de renderização específicos
  if (screenId === 'home') {
    renderHomeFeed();
    trackEvent('app_opened', { view: 'home' });
  } else if (screenId === 'demand-service') {
    trackEvent('on_demand_flow_started', { category: AppState.selectedDemandCategory });
  } else if (screenId === 'uber-demand') {
    renderUberDemandMap();
    trackEvent('uber_demand_map_opened', { category: AppState.selectedDemandCategory });
  } else if (screenId === 'map') {
    renderMap();
    trackEvent('search_performed', { 
      filter: AppState.activeFilter,
      query: '',
      results_count: MOCK_SALONS.length
    });
  } else if (screenId === 'detail') {
    renderDetailScreen();
    trackEvent('merchant_viewed', { 
      merchant_id: AppState.selectedSalon.id, 
      distance_km: AppState.selectedSalon.distanceKm,
      has_discount_today: getSalonPromoRules(AppState.selectedSalon, AppState.selectedDate).length > 0
    });
  } else if (screenId === 'checkout') {
    renderCheckoutScreen();
    startReservationTimer();
    const finalPrice = AppState.currentPricing ? AppState.currentPricing.finalPrice : 84.00;
    
    // Transição de estado: PENDING_PAYMENT (Seção 8 e Bloco B5)
    const tempAppointmentId = 'BP-' + Math.floor(100000 + Math.random() * 900000);
    AppState._pendingCheckoutApptId = tempAppointmentId;
    emitAppointmentStatusChanged(tempAppointmentId, 'NONE', 'PENDING_PAYMENT');

    trackEvent('checkout_started', { 
      total: finalPrice,
      appointment_id: tempAppointmentId
    });
  } else if (screenId === 'confirm') {
    renderConfirmScreen();
  } else if (screenId === 'appointments') {
    renderAppointmentsScreen();
  } else if (screenId === 'profile') {
    updateUserUI();
    renderValidationMetrics();
  }
}

// ===================================================================
// 6. RENDERIZADORES DE TELAS
// ===================================================================

// --- SCREEN 1: HOME FEED ---

function isSalonCategoryMatch(salon, categoryKey) {
  if (!categoryKey || categoryKey === 'all') return true;
  const matchCategory = (targetCat) => {
    if (!targetCat) return false;
    if (categoryKey === 'esthetic') return targetCat === 'esthetic' || targetCat === 'facial';
    return targetCat === categoryKey;
  };
  if (matchCategory(salon.category)) return true;
  if (salon.services && salon.services.some(srv => matchCategory(srv.category))) return true;
  return false;
}

function getContextualService(salon, categoryKey) {
  if (!categoryKey || categoryKey === 'all') {
    return (salon.services && salon.services.length > 0) ? salon.services[0] : salon.service;
  }
  const matchCategory = (targetCat) => {
    if (!targetCat) return false;
    if (categoryKey === 'esthetic') return targetCat === 'esthetic' || targetCat === 'facial';
    return targetCat === categoryKey;
  };
  if (salon.services && salon.services.length > 0) {
    const found = salon.services.find(srv => matchCategory(srv.category));
    if (found) return found;
  }
  return salon.service;
}

function getContextualSalonImage(salon, categoryKey) {
  // Preserva a identidade fotográfica autêntica e exclusiva de cada estabelecimento!
  // Se o serviço contextual tiver uma imagem própria cadastrada, prioriza ela; caso contrário, mantém a foto do salão.
  const currentService = getContextualService(salon, categoryKey);
  if (currentService && currentService.image) {
    return currentService.image;
  }
  return salon.image || 'salon_hair_boutique.jpg';
}

function selectHomeCategory(categoryKey, btnEl) {
  document.querySelectorAll('.categories-carousel .category-chip').forEach(c => c.classList.remove('active'));
  if (btnEl) {
    btnEl.classList.add('active');
  } else {
    const defaultBtn = document.querySelector(`.categories-carousel .category-chip[onclick*="'${categoryKey}'"]`);
    if (defaultBtn) defaultBtn.classList.add('active');
  }
  
  AppState.activeCategory = categoryKey;
  renderHomeFeed();
  
  trackEvent('search_performed', {
    filter: 'category_chip',
    query: categoryKey,
    results_count: getFilteredSalons().length
  });
}

function setQuickFilter(type, btnEl) {
  document.querySelectorAll('.filter-chips-row .filter-chip').forEach(b => b.classList.remove('active'));
  if (btnEl) {
    btnEl.classList.add('active');
  } else {
    const targetBtn = document.querySelector(`.filter-chips-row .filter-chip[onclick*="'${type}'"]`);
    if (targetBtn) targetBtn.classList.add('active');
  }
  
  AppState.activeFilter = type;
  renderHomeFeed();
}

function clearAllHomeFilters() {
  AppState.activeCategory = 'all';
  AppState.activeFilter = 'all';
  AppState.homeSearchQuery = '';
  
  const searchInput = document.getElementById('home-search-input');
  if (searchInput) searchInput.value = '';
  
  document.querySelectorAll('.categories-carousel .category-chip').forEach(c => c.classList.remove('active'));
  const allCatBtn = document.querySelector(".categories-carousel .category-chip[onclick*=\"'all'\"]");
  if (allCatBtn) allCatBtn.classList.add('active');
  
  document.querySelectorAll('.filter-chips-row .filter-chip').forEach(b => b.classList.remove('active'));
  const allFilterBtn = document.querySelector(".filter-chips-row .filter-chip[onclick*=\"'all'\"]");
  if (allFilterBtn) allFilterBtn.classList.add('active');
  
  renderHomeFeed();
}

function onHomeSearch(query) {
  AppState.homeSearchQuery = (query || '').trim().toLowerCase();
  renderHomeFeed();
  
  if (AppState.homeSearchQuery.length >= 2) {
    trackEvent('search_performed', {
      filter: 'home_search_bar',
      query: AppState.homeSearchQuery,
      results_count: getFilteredSalons().length
    });
  }
}

function getFilteredSalons() {
  let list = MOCK_SALONS;
  
  // 1. Filtro estrito de Categoria
  if (AppState.activeCategory && AppState.activeCategory !== 'all') {
    list = list.filter(salon => isSalonCategoryMatch(salon, AppState.activeCategory));
  }
  
  // 2. Filtro de Busca por Texto
  if (AppState.homeSearchQuery) {
    const q = AppState.homeSearchQuery;
    list = list.filter(salon => {
      const matchName = salon.name.toLowerCase().includes(q);
      const matchBairro = salon.neighborhood.toLowerCase().includes(q);
      const matchServices = salon.services && salon.services.some(srv => 
        srv.name.toLowerCase().includes(q) || srv.description.toLowerCase().includes(q)
      );
      return matchName || matchBairro || matchServices;
    });
  }
  
  // 3. Filtro Rápido e Ordenação Explícita
  if (AppState.activeFilter === 'economy') {
    list = [...list].sort((a, b) => {
      const maxDiscountA = Math.max(...(a.discountSlots || []).map(s => s.discountPct), 0);
      const maxDiscountB = Math.max(...(b.discountSlots || []).map(s => s.discountPct), 0);
      if (maxDiscountB !== maxDiscountA) return maxDiscountB - maxDiscountA;
      return a.service.basePrice - b.service.basePrice;
    });
  } else if (AppState.activeFilter === 'proximity') {
    list = [...list].sort((a, b) => a.distanceKm - b.distanceKm);
  }
  
  return list;
}

function renderHomeFeed() {
  const feedContainer = document.getElementById('social-feed-container');
  if (!feedContainer) return;

  const statusBar = document.getElementById('feed-status-bar');
  const salonsToDisplay = getFilteredSalons();

  // Atualização da Barra de Feedback de Filtros Ativos
  if (statusBar) {
    const hasCategory = AppState.activeCategory && AppState.activeCategory !== 'all';
    const hasFilter = AppState.activeFilter && AppState.activeFilter !== 'all';
    const hasSearch = Boolean(AppState.homeSearchQuery);

    if (hasCategory || hasFilter || hasSearch) {
      statusBar.style.display = 'flex';
      
      const catLabels = {
        hair: 'Cabelo',
        nails: 'Unhas',
        barber: 'Barbearia',
        massage: 'Massagem',
        esthetic: 'Estética'
      };

      let filterDesc = '';
      if (hasCategory && hasFilter) {
        const filterName = AppState.activeFilter === 'proximity' ? 'Mais Próximos' : 'Maior Economia';
        filterDesc = `Filtrando por <strong>${catLabels[AppState.activeCategory] || AppState.activeCategory}</strong> • <strong>${filterName}</strong>`;
      } else if (hasCategory) {
        filterDesc = `Mostrando <strong>${salonsToDisplay.length} estabelecimentos</strong> com <strong>${catLabels[AppState.activeCategory] || AppState.activeCategory}</strong>`;
      } else if (AppState.activeFilter === 'proximity') {
        filterDesc = `Ordenado por <strong>Mais Próximos de você</strong> (&lt; 2 km primeiro)`;
      } else if (AppState.activeFilter === 'economy') {
        filterDesc = `Ordenado por <strong>Maior Economia</strong> (Até 35% de desconto)`;
      } else if (hasSearch) {
        filterDesc = `Resultados para <strong>"${AppState.homeSearchQuery}"</strong> (${salonsToDisplay.length})`;
      }

      statusBar.innerHTML = `
        <div class="feed-status-content">
          <span class="feed-status-icon">
            <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><polygon points="22 3 2 3 10 12.46 10 19 14 21 14 12.46 22 3"/></svg>
          </span>
          <span>${filterDesc}</span>
        </div>
        <button class="feed-status-clear" onclick="clearAllHomeFilters()">
          <svg width="11" height="11" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><path d="M18 6L6 18M6 6l12 12"/></svg>
          <span>Limpar</span>
        </button>
      `;
    } else {
      statusBar.style.display = 'none';
      statusBar.innerHTML = '';
    }
  }

  feedContainer.innerHTML = '';

  if (salonsToDisplay.length === 0) {
    feedContainer.innerHTML = `
      <div style="text-align:center; padding:32px 16px; color:var(--neutral-muted);">
        <svg width="36" height="36" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" style="margin-bottom:8px; opacity:0.6;"><circle cx="11" cy="11" r="8"/><path d="M21 21l-4.35-4.35"/></svg>
        <p style="font-size:13px; font-weight:700;">Nenhum estabelecimento encontrado</p>
        <p style="font-size:11px; margin-top:4px;">Tente selecionar outra categoria ou limpar os filtros ativos.</p>
        <button class="card-action-btn" style="margin-top:14px; max-width:200px; margin-inline:auto;" onclick="clearAllHomeFilters()">Limpar Filtros</button>
      </div>
    `;
    return;
  }

  salonsToDisplay.forEach(salon => {
    // 1. Serviço e foto contextuais com base na categoria ativa
    const currentService = getContextualService(salon, AppState.activeCategory);
    const cardImage = getContextualSalonImage(salon, AppState.activeCategory);

    // 2. Preço calculado para o serviço específico
    const bestPromo = salon.discountSlots ? salon.discountSlots[0] : null;
    const pricing = bestPromo ? calculateSlotPrice(salon, bestPromo.time, currentService) : calculateSlotPrice(salon, '10:00', currentService);

    // 3. Badges de prioridade explícita
    let priorityBadgeHtml = '';
    if (AppState.activeFilter === 'proximity') {
      priorityBadgeHtml = `
        <div class="card-priority-badge priority-proximity">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><polygon points="16.24 7.76 14.12 14.12 7.76 16.24 9.88 9.88 16.24 7.76"/></svg>
          <span>${salon.distanceKm} km de você</span>
        </div>
      `;
    } else if (AppState.activeFilter === 'economy') {
      const topDiscount = Math.max(...(salon.discountSlots || []).map(s => s.discountPct), 0);
      priorityBadgeHtml = `
        <div class="card-priority-badge priority-economy">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><path d="M20.59 13.41l-7.17 7.17a2 2 0 0 1-2.83 0L2 12V2h10l8.59 8.59a2 2 0 0 1 0 2.82z"/><line x1="7" y1="7" x2="7.01" y2="7"/></svg>
          <span>Melhor Desconto (${topDiscount}% OFF)</span>
        </div>
      `;
    }

    // 4. Selo do Responsável Técnico / Fundador
    const leadStaff = salon.leadStaff || {
      name: "Juliana Paes",
      role: "Fundadora & Master Stylist",
      avatar: "profile_owner_juliana.jpg",
      verified: true
    };

    // 5. Cluster da Rede Social ("Quem me conhece frequenta")
    const mutualNetwork = salon.mutualNetwork || {
      avatars: ["friend_camila.jpg", "friend_beatriz.jpg", "friend_larissa.jpg"],
      text: "Camila, Beatriz e +1 amiga sua frequentam este espaço"
    };

    // Texto limpo de prova social (ex: "Camila agendou há 14 min")
    const cleanSocialProofText = (salon.socialProof || '').replace(/^#/, '');

    const card = document.createElement('div');
    card.className = 'social-salon-card';
    card.innerHTML = `
      <div class="card-media-wrap">
        <img src="${cardImage}" alt="${salon.name}" loading="lazy" onerror="this.onerror=null; this.src='salon_hair_boutique.jpg'">
        <div class="social-proof-pill">
          <span class="live-pulse-dot"></span>
          <span>${cleanSocialProofText}</span>
        </div>
        <div class="card-eta-badge">
          <svg width="10" height="10" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><circle cx="12" cy="12" r="10"/><polygon points="16.24 7.76 14.12 14.12 7.76 16.24 9.88 9.88 16.24 7.76"/></svg>
          <span>${salon.distanceKm} km</span>
        </div>
        ${priorityBadgeHtml}
      </div>
      <div class="card-body">
        <div class="card-header-line">
          <h3 class="salon-name">
            ${salon.name}
            <svg class="verified-badge" viewBox="0 0 24 24" fill="currentColor">
              <path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm-2 15l-5-5 1.41-1.41L10 14.17l7.59-7.59L19 8l-9 9z"/>
            </svg>
          </h3>
          <div class="rating-chip">
            <svg viewBox="0 0 24 24"><path d="M12 17.27L18.18 21l-1.64-7.03L22 9.24l-7.19-.61L12 2 9.19 8.63 2 9.24l5.46 4.73L5.82 21z"/></svg>
            <span>${salon.rating}</span>
          </div>
        </div>

        <!-- Selo do Responsável pelo Estabelecimento -->
        <div class="card-owner-seal">
          <img class="owner-seal-avatar" src="${leadStaff.avatar}" alt="${leadStaff.name}" loading="lazy" onerror="this.onerror=null; this.src='profile_owner_juliana.jpg'">
          <div class="owner-seal-info">
            <div class="owner-seal-name-row">
              <span class="owner-seal-name">${leadStaff.name}</span>
              <svg class="owner-seal-badge" width="13" height="13" viewBox="0 0 24 24" fill="currentColor">
                <path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm-2 15l-5-5 1.41-1.41L10 14.17l7.59-7.59L19 8l-9 9z"/>
              </svg>
            </div>
            <span class="owner-seal-role">${leadStaff.role}</span>
          </div>
        </div>

        <!-- Cluster Social ("Quem me conhece frequenta") -->
        <div class="card-mutual-friends">
          <div class="mutual-avatars-stack">
            ${mutualNetwork.avatars.map(av => `<img class="mutual-avatar-item" src="${av}" alt="amiga" loading="lazy" onerror="this.onerror=null; this.src='friend_camila.jpg'">`).join('')}
          </div>
          <span class="mutual-friends-text">${mutualNetwork.text}</span>
        </div>

        <p class="salon-service-desc">
          <span>${currentService.name}</span> • <span>${currentService.durationMinutes} min</span>
        </p>

        <!-- Dynamic Pricing Box (Strict Section 6.2) -->
        <div class="pricing-block">
          <div class="price-details">
            <div class="price-values-row">
              ${pricing.hasDiscount ? `<span class="price-base-slashed">R$ ${pricing.basePrice.toFixed(2)}</span>` : ''}
              <span class="price-final-bold">R$ ${pricing.finalPrice.toFixed(2)}</span>
              ${pricing.hasDiscount ? `<span class="discount-tag">-${pricing.discountPct}%</span>` : ''}
            </div>
            <span class="pricing-subtext">${pricing.subtext}</span>
          </div>
          ${pricing.hasDiscount ? `
            <span class="badge-tag-dynamic ${pricing.type === 'urgent' ? 'urgent' : ''}">
              ${pricing.badgeText}
            </span>
          ` : ''}
        </div>

        <button class="card-action-btn" onclick="openSalonDetail('${salon.id}', '${currentService.id}')">
          <span>Escolher Horário</span>
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><path d="M5 12h14M12 5l7 7-7 7"/></svg>
        </button>
      </div>
    `;
    feedContainer.appendChild(card);
  });
}

function openSalonDetail(salonId, serviceId = null) {
  const salon = MOCK_SALONS.find(s => s.id === salonId);
  if (salon) {
    AppState.selectedSalon = salon;
    if (serviceId && salon.services) {
      const matched = salon.services.find(s => s.id === serviceId);
      AppState.selectedService = matched || salon.services[0];
    } else {
      AppState.selectedService = (salon.services && salon.services.length > 0) ? salon.services[0] : salon.service;
    }
    AppState.selectedStaff = { id: 'any', name: 'Qualquer Profissional', role: 'Primeiro disponível', isAny: true };
    navigateTo('detail');
  }
}

// --- SCREEN 2: GEO-DISCOVERY & LEAFLET MAP ---
let leafletMapInstance = null;
let currentRoutePolyline = null;
let mapMarkersList = [];
let userMarkerRef = null;

function renderMap() {
  const mapContainer = document.getElementById('leaflet-map');
  if (!mapContainer) return;

  if (!leafletMapInstance) {
    leafletMapInstance = L.map('leaflet-map', {
      zoomControl: false,
      attributionControl: false,
      minZoom: 11,
      maxZoom: 18
    }).setView([-23.5650, -46.6810], 13);

    // OpenStreetMap tiles — gratuito, sem necessidade de API key
    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
      maxZoom: 19
    }).addTo(leafletMapInstance);

    // Marcador do Usuário (GPS Pulse)
    const userPin = L.divIcon({
      className: 'user-gps-container',
      html: `
        <div class="user-gps-node" title="Sua localização atual">
          <div class="user-gps-dot"></div>
          <div class="user-gps-pulse"></div>
        </div>
      `,
      iconSize: [26, 26],
      iconAnchor: [13, 13]
    });
    userMarkerRef = L.marker([-23.5650, -46.6810], { icon: userPin }).addTo(leafletMapInstance);

    // Solicitar geolocalização real do dispositivo (Seção 6.4)
    if (navigator.geolocation) {
      navigator.geolocation.getCurrentPosition(
        (pos) => {
          const { latitude, longitude } = pos.coords;
          if (userMarkerRef) userMarkerRef.setLatLng([latitude, longitude]);
          leafletMapInstance.setView([latitude, longitude], 14);
          const warning = document.getElementById('map-location-warning');
          if (warning) warning.style.display = 'none';
          trackEvent('location_permission', { granted: true });
        },
        () => {
          // Fallback: manter centro de SP e exibir aviso obrigatório (Seção 6.4)
          showMapLocationFallback();
          trackEvent('location_permission', { granted: false });
        },
        { timeout: 5000, enableHighAccuracy: false }
      );
    } else {
      showMapLocationFallback();
    }

    // Adiciona Marcadores de Localidade Minimalistas (sem emojis)
    renderLocalityMarkers(MOCK_SALONS);
  }

  // Garante que o mapa recalcula as dimensões imediatamente na aba
  setTimeout(() => {
    if (leafletMapInstance) {
      leafletMapInstance.invalidateSize();
    }
  }, 50);
  setTimeout(() => {
    if (leafletMapInstance) {
      leafletMapInstance.invalidateSize();
    }
  }, 250);

  renderMapBottomSheet(MOCK_SALONS);
}

function showMapLocationFallback() {
  const warning = document.getElementById('map-location-warning');
  if (warning) warning.style.display = 'flex';
}

function renderLocalityMarkers(salons) {
  if (!leafletMapInstance) return;

  // Limpa marcadores anteriores
  mapMarkersList.forEach(m => leafletMapInstance.removeLayer(m));
  mapMarkersList = [];

  salons.forEach(salon => {
    const bestPromo = salon.discountSlots[0];
    const hasDiscount = !!bestPromo;

    const localityIcon = L.divIcon({
      className: 'locality-marker-wrap',
      html: `
        <div class="locality-node ${hasDiscount ? 'active-deal' : ''}">
          <div class="locality-dot"></div>
          <div class="locality-ring"></div>
        </div>
        <div class="locality-tag-pill">
          <span>${salon.neighborhood}</span>
          ${hasDiscount ? `<span class="locality-discount-badge">-${bestPromo.discountPct}%</span>` : ''}
        </div>
      `,
      iconSize: [110, 48],
      iconAnchor: [55, 12]
    });

    const marker = L.marker([salon.lat, salon.lng], { icon: localityIcon }).addTo(leafletMapInstance);
    marker.on('click', () => selectMapSalon(salon.id));
    mapMarkersList.push(marker);
  });
}

function renderMapBottomSheet(salons = MOCK_SALONS) {
  const container = document.getElementById('sheet-salons-list');
  if (!container) return;

  container.innerHTML = '';
  if (salons.length === 0) {
    container.innerHTML = `<div style="padding:14px; font-size:12px; color:var(--neutral-muted);">Nenhum salão encontrado para esta localidade.</div>`;
    return;
  }

  salons.forEach(salon => {
    const card = document.createElement('div');
    card.id = `sheet-card-${salon.id}`;
    card.className = 'mini-salon-card';
    card.onclick = () => selectMapSalon(salon.id);
    card.innerHTML = `
      <div class="mini-card-thumb">
        <img src="${salon.image}" alt="${salon.name}" onerror="this.src='treatment_hair_salon.jpg'">
      </div>
      <div class="mini-card-body">
        <h4 class="mini-salon-name">${salon.name}</h4>
        <div class="mini-salon-meta">
          <span style="display:inline-flex; align-items:center; gap:3px;"><svg width="10" height="10" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><path d="M12 2C8.13 2 5 5.13 5 9c0 5.25 7 13 7 13s7-7.75 7-13c0-3.87-3.13-7-7-7z"/><circle cx="12" cy="9" r="2.5"/></svg>${salon.neighborhood} · ${salon.distanceKm} km</span>
          <span style="font-weight:700; color:var(--primary);">A partir de R$ ${((salon.services && salon.services[0]) || salon.service).basePrice.toFixed(2)}</span>
        </div>
      </div>
    `;
    container.appendChild(card);
  });
}

function selectMapSalon(salonId) {
  const salon = MOCK_SALONS.find(s => s.id === salonId);
  if (!salon || !leafletMapInstance) return;

  // Traçado Dinâmico de Rota em Verde-Esmeralda (Mobilidade & Corridas Autônomas)
  if (currentRoutePolyline) {
    leafletMapInstance.removeLayer(currentRoutePolyline);
  }

  const userCoords = [-23.5650, -46.6810];
  const salonCoords = [salon.lat, salon.lng];

  // Gera arco curvilíneo fluido (Triple Fusion: Mobilidade & Corridas Autônomas)
  const midLat = (userCoords[0] + salonCoords[0]) / 2;
  const midLng = (userCoords[1] + salonCoords[1]) / 2;
  const dLat = salonCoords[0] - userCoords[0];
  const dLng = salonCoords[1] - userCoords[1];
  // Ponto de controle perpendicular suave
  const controlPoint = [midLat - dLng * 0.22, midLng + dLat * 0.22];

  // Interpolação Bézier quadrática com 25 passos
  const curvedPoints = [];
  const steps = 24;
  for (let i = 0; i <= steps; i++) {
    const t = i / steps;
    const lat = (1 - t) * (1 - t) * userCoords[0] + 2 * (1 - t) * t * controlPoint[0] + t * t * salonCoords[0];
    const lng = (1 - t) * (1 - t) * userCoords[1] + 2 * (1 - t) * t * controlPoint[1] + t * t * salonCoords[1];
    curvedPoints.push([lat, lng]);
  }

  currentRoutePolyline = L.polyline(curvedPoints, {
    color: '#00685F',
    weight: 4.5,
    opacity: 0.9,
    dashArray: '6, 6',
    lineCap: 'round',
    lineJoin: 'round'
  }).addTo(leafletMapInstance);

  // Centralização suave com animação (flyTo)
  leafletMapInstance.flyTo(salonCoords, 15, {
    animate: true,
    duration: 0.8
  });

  // Destaque do card no bottom sheet
  document.querySelectorAll('.mini-salon-card').forEach(c => c.style.borderColor = 'var(--outline-variant)');
  const targetCard = document.getElementById(`sheet-card-${salon.id}`);
  if (targetCard) {
    targetCard.style.borderColor = 'var(--primary)';
    targetCard.scrollIntoView({ behavior: 'smooth', block: 'nearest', inline: 'center' });
  }

  trackEvent('locality_selected', { neighborhood: salon.neighborhood, merchant_id: salon.id });
}

function onMapSearch(query) {
  const cleanQ = (query || '').trim().toLowerCase();
  if (!cleanQ) {
    renderMapBottomSheet(MOCK_SALONS);
    renderLocalityMarkers(MOCK_SALONS);
    if (currentRoutePolyline && leafletMapInstance) {
      leafletMapInstance.removeLayer(currentRoutePolyline);
      currentRoutePolyline = null;
    }
    return;
  }

  const matched = MOCK_SALONS.filter(s =>
    s.name.toLowerCase().includes(cleanQ) ||
    s.neighborhood.toLowerCase().includes(cleanQ) ||
    s.address.toLowerCase().includes(cleanQ)
  );

  trackEvent('search_performed', {
    filter: 'map_query',
    query: cleanQ,
    results_count: matched.length
  });

  renderMapBottomSheet(matched);
  renderLocalityMarkers(matched);

  if (matched.length > 0) {
    selectMapSalon(matched[0].id);
  }
}

// ===================================================================
// 6.1 FLUXO ON-DEMAND COM MAPA E ROTA DE CAMINHADA ESTILO UBER
// ===================================================================

const DEMAND_CATEGORY_INFO = {
  hair: {
    label: 'Cabelo & Escova',
    iconSvg: '<circle cx="6" cy="6" r="3"/><circle cx="6" cy="18" r="3"/><line x1="20" y1="4" x2="8.12" y2="15.88"/><line x1="14.47" y1="14.48" x2="20" y2="20"/><line x1="8.12" y1="8.12" x2="12" y2="12"/><path d="M15 7c1.5-1 3.5-1 5 0"/><path d="M14 10.5c1.8-.8 3.8-.8 5.5 0"/>'
  },
  nails: {
    label: 'Unhas & Manicure',
    iconSvg: '<path d="M10 2h4v5h-4z"/><path d="M7 10a2 2 0 0 1 2-2h6a2 2 0 0 1 2 2v9a2 2 0 0 1-2 2H9a2 2 0 0 1-2-2v-9z"/><path d="M10 13h4"/><path d="M10 16.5h2.5"/><path d="M19 4v2m-1-1h2"/>'
  },
  barber: {
    label: 'Barba & Corte',
    iconSvg: '<path d="M3 6h11a1.5 1.5 0 0 1 1.5 1.5v1.5a1.5 1.5 0 0 1-1.5 1.5H3V6z"/><line x1="3" y1="9" x2="13.5" y2="9"/><circle cx="16" cy="9" r="1.5" fill="currentColor"/><path d="M16 10.5c1.5 2.5 3 6 4 10a1.5 1.5 0 0 1-2 1.8c-2.5-1.5-5-4.5-6.5-7.5"/>'
  },
  massage: {
    label: 'Massagem & Spa',
    iconSvg: '<path d="M12 4c-1.8 3-3 6.5-3 9.5 0 2.5 1.3 4 3 4s3-1.5 3-4c0-3-1.2-6.5-3-9.5z"/><path d="M9 13.5C6.5 11.5 4 12 3 13.5c-.5 1.8 1 4 4.5 4.5"/><path d="M15 13.5c2.5-2 5-1.5 6 0 .5 1.8-1 4-4.5 4.5"/><path d="M4 21c4-1 12-1 16 0"/>'
  },
  facial: {
    label: 'Estética Facial',
    iconSvg: '<path d="M8 3c3 0 5.5 1.8 6 4.5l.8 3c.3 1 .1 2-.6 2.5l-.2.1c-.5.4-.7 1-.5 1.6l.3 1c.3.9-.2 1.8-1.1 2.1-1.2.4-2.7.7-4.7.7"/><path d="M9 10.5c.8.5 1.7.5 2.5 0"/><path d="M18 3v4m-2-2h4"/><path d="M19 12v2m-1-1h2"/>'
  }
};

const DEMAND_WINDOW_LABELS = {
  now_2h: 'Próximas 2h',
  today_afternoon: 'Hoje à Tarde',
  today_evening: 'Hoje à Noite'
};

function openOnDemandFlow() {
  trackEvent('on_demand_flow_started', { source: 'nav_or_banner' });
  navigateTo('demand-service');
}

function selectDemandService(category, cardEl) {
  AppState.selectedDemandCategory = category;
  document.querySelectorAll('.demand-service-card').forEach(c => c.classList.remove('active'));
  if (cardEl) {
    cardEl.classList.add('active');
  } else {
    const card = document.querySelector(`.demand-service-card[data-category="${category}"]`);
    if (card) card.classList.add('active');
  }
}

function selectDemandWindow(windowKey, btnEl) {
  AppState.selectedDemandWindow = windowKey;
  document.querySelectorAll('#demand-window-chips .window-chip').forEach(b => b.classList.remove('active'));
  if (btnEl) btnEl.classList.add('active');
}

function launchUberDemandMap() {
  const info = DEMAND_CATEGORY_INFO[AppState.selectedDemandCategory] || DEMAND_CATEGORY_INFO.hair;
  const winLabel = DEMAND_WINDOW_LABELS[AppState.selectedDemandWindow] || 'Próximas 2h';

  const srvLabelEl = document.getElementById('uber-active-service-name');
  const winLabelEl = document.getElementById('uber-active-window-name');
  if (srvLabelEl) srvLabelEl.textContent = info.label;
  if (winLabelEl) winLabelEl.textContent = winLabel;

  navigateTo('uber-demand');
}

function getDemandCandidateSalons(category) {
  let candidates = MOCK_SALONS.filter(s => {
    if (s.category === category) return true;
    if (s.services && s.services.some(srv => srv.category === category)) return true;
    return false;
  });

  if (candidates.length < 2) {
    candidates = MOCK_SALONS.slice(0, 3);
  }

  return candidates.slice(0, 3).map((salon, idx) => {
    let service = (salon.services && salon.services.find(srv => srv.category === category)) 
      || (salon.services && salon.services[0]) 
      || salon.service;
    
    // Descontos calibrados por ociosidade (35%, 30%, 25%)
    const discountPcts = [35, 30, 25];
    const discountPct = discountPcts[idx % discountPcts.length];
    const basePrice = service.basePrice || 120;
    const finalPrice = Math.round(basePrice * (1 - discountPct / 100));

    // Distância e estimativa a pé em minutos
    const distanceKm = salon.distanceKm || (0.6 + idx * 0.3);
    const walkDistanceM = Math.round(distanceKm * 1000);
    const walkTimeMin = Math.max(4, Math.round(distanceKm * 12));

    return {
      salon,
      service,
      pricing: {
        hasDiscount: true,
        isOccupied: false,
        type: 'economy',
        discountPct: discountPct,
        basePrice: basePrice,
        finalPrice: finalPrice,
        badgeText: `${discountPct}% OFF`,
        subtext: `Cadeira ociosa encaixe imediato`
      },
      walkDistanceM,
      walkTimeMin,
      availableStaff: (salon.staff && salon.staff[0]) || { name: 'Profissional da Cadeira Ociosa' }
    };
  });
}

function renderUberDemandMap() {
  const mapContainer = document.getElementById('uber-demand-map');
  if (!mapContainer) return;

  const matches = getDemandCandidateSalons(AppState.selectedDemandCategory);
  AppState.uberMatchesList = matches;

  const userCoords = [-23.5650, -46.6810];

  if (!AppState.uberMapInstance) {
    AppState.uberMapInstance = L.map('uber-demand-map', {
      zoomControl: false,
      attributionControl: false,
      minZoom: 11,
      maxZoom: 18
    }).setView(userCoords, 14);

    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
      maxZoom: 19
    }).addTo(AppState.uberMapInstance);

    // Marcador do Pedestre (Usuário caminhante com pulso)
    const pedestrianIcon = L.divIcon({
      className: 'uber-pedestrian-icon-wrap',
      html: `
        <div class="uber-pedestrian-node">
          <div class="uber-pedestrian-pulse"></div>
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5">
            <circle cx="12" cy="5" r="2.5"/>
            <path d="M10 22v-6l-2-2.5 1.5-3.5L13 12v10"/>
            <path d="M14 12l2.5 3.5"/>
          </svg>
        </div>
      `,
      iconSize: [36, 36],
      iconAnchor: [18, 18]
    });
    L.marker(userCoords, { icon: pedestrianIcon }).addTo(AppState.uberMapInstance);
  }

  // Limpa marcadores anteriores dos salões
  if (AppState.uberMarkersList && AppState.uberMarkersList.length > 0) {
    AppState.uberMarkersList.forEach(m => AppState.uberMapInstance.removeLayer(m));
  }
  AppState.uberMarkersList = [];

  // Adiciona marcadores flutuantes com balão de preço
  matches.forEach((m, idx) => {
    const isSelected = idx === 0;
    const bubbleIcon = L.divIcon({
      className: 'uber-bubble-wrapper',
      html: `
        <div class="uber-price-bubble ${isSelected ? 'selected-bubble' : ''}" id="uber-bubble-${m.salon.id}">
          <span>R$ ${m.pricing.finalPrice}</span>
          <span class="bubble-deal">-${m.pricing.discountPct}%</span>
        </div>
      `,
      iconSize: [85, 34],
      iconAnchor: [42, 34]
    });

    const marker = L.marker([m.salon.lat, m.salon.lng], { icon: bubbleIcon }).addTo(AppState.uberMapInstance);
    marker.on('click', () => selectDemandMatch(m.salon.id));
    AppState.uberMarkersList.push(marker);
  });

  // Renderiza matches na gaveta inferior (bottom sheet)
  renderUberMatchesSheet(matches);

  // Seleciona o primeiro salão (melhor economia / mais próximo)
  if (matches.length > 0) {
    selectDemandMatch(matches[0].salon.id, false);
  }

  // Recalcular dimensões do mapa (essencial para renderizar tiles Leaflet)
  setTimeout(() => {
    if (AppState.uberMapInstance) {
      AppState.uberMapInstance.invalidateSize();
    }
  }, 60);
  setTimeout(() => {
    if (AppState.uberMapInstance) {
      AppState.uberMapInstance.invalidateSize();
    }
  }, 280);
}

function renderUberMatchesSheet(matches) {
  const container = document.getElementById('uber-demand-matches-list');
  const countEl = document.getElementById('uber-sheet-match-count');
  if (!container) return;

  if (countEl) {
    countEl.textContent = `${matches.length} opções com encaixe imediato e tarifa calibrada`;
  }

  container.innerHTML = '';
  matches.forEach((m, idx) => {
    const isSelected = idx === 0;
    const savings = m.pricing.basePrice - m.pricing.finalPrice;
    const item = document.createElement('div');
    item.className = `uber-match-item ${isSelected ? 'selected' : ''}`;
    item.id = `uber-match-item-${m.salon.id}`;
    item.onclick = () => selectDemandMatch(m.salon.id);

    item.innerHTML = `
      <div class="uber-item-left">
        <img class="uber-item-thumb" src="${m.salon.image}" alt="${m.salon.name}">
        <div>
          <div class="uber-item-name">${m.salon.name}</div>
          <div class="uber-item-walk">
            <span>🚶</span>
            <span><strong>${m.walkTimeMin} min</strong> a pé (${m.walkDistanceM} m)</span>
          </div>
        </div>
      </div>
      <div class="uber-item-right">
        <span class="uber-base-slashed">R$ ${m.pricing.basePrice.toFixed(2)}</span>
        <span class="uber-fair-price">R$ ${m.pricing.finalPrice.toFixed(2)}</span>
        <span class="uber-savings-pill">Economize R$ ${savings.toFixed(0)} (${m.pricing.discountPct}% OFF)</span>
      </div>
    `;
    container.appendChild(item);
  });
}

function selectDemandMatch(salonId, shouldFly = true) {
  const match = AppState.uberMatchesList.find(m => m.salon.id === salonId);
  if (!match || !AppState.uberMapInstance) return;

  AppState.selectedDemandMatch = match;
  AppState.selectedSalon = match.salon;
  AppState.selectedService = match.service;
  AppState.currentPricing = match.pricing;
  AppState.lastWalkTimeMin = match.walkTimeMin;
  AppState.lastWalkDistanceM = match.walkDistanceM;
  AppState.selectedStaff = { 
    id: 'any', 
    name: 'Qualquer Profissional Disponível', 
    role: 'Cadeira Ociosa (Encaixe Imediato)', 
    isAny: true 
  };
  AppState.selectedTime = '14:00';

  // Atualiza destaque na gaveta
  document.querySelectorAll('.uber-match-item').forEach(el => el.classList.remove('selected'));
  const activeItem = document.getElementById(`uber-match-item-${salonId}`);
  if (activeItem) activeItem.classList.add('selected');

  // Atualiza balões de preço no mapa
  document.querySelectorAll('.uber-price-bubble').forEach(b => b.classList.remove('selected-bubble'));
  const activeBubble = document.getElementById(`uber-bubble-${salonId}`);
  if (activeBubble) activeBubble.classList.add('selected-bubble');

  // Remove rota anterior e badge
  if (AppState.uberRoutePolyline) {
    AppState.uberMapInstance.removeLayer(AppState.uberRoutePolyline);
    AppState.uberRoutePolyline = null;
  }
  if (AppState.uberWalkingBadgeMarker) {
    AppState.uberMapInstance.removeLayer(AppState.uberWalkingBadgeMarker);
    AppState.uberWalkingBadgeMarker = null;
  }

  // Traça Rota Curvilínea Suave em Verde-Esmeralda (Mobilidade)
  const userCoords = [-23.5650, -46.6810];
  const salonCoords = [match.salon.lat, match.salon.lng];

  const midLat = (userCoords[0] + salonCoords[0]) / 2;
  const midLng = (userCoords[1] + salonCoords[1]) / 2;
  const dLat = salonCoords[0] - userCoords[0];
  const dLng = salonCoords[1] - userCoords[1];
  const controlPoint = [midLat - dLng * 0.22, midLng + dLat * 0.22];

  const curvedPoints = [];
  const steps = 24;
  for (let i = 0; i <= steps; i++) {
    const t = i / steps;
    const lat = (1 - t) * (1 - t) * userCoords[0] + 2 * (1 - t) * t * controlPoint[0] + t * t * salonCoords[0];
    const lng = (1 - t) * (1 - t) * userCoords[1] + 2 * (1 - t) * t * controlPoint[1] + t * t * salonCoords[1];
    curvedPoints.push([lat, lng]);
  }

  AppState.uberRoutePolyline = L.polyline(curvedPoints, {
    color: '#00685F',
    weight: 4.5,
    opacity: 0.95,
    dashArray: '7, 6',
    lineCap: 'round',
    lineJoin: 'round'
  }).addTo(AppState.uberMapInstance);

  // Badge Flutuante no Centro da Rota (🚶 X min • XXX m)
  const midIndex = Math.floor(curvedPoints.length / 2);
  const midPoint = curvedPoints[midIndex];

  const badgeIcon = L.divIcon({
    className: 'uber-walking-badge-wrap',
    html: `
      <div class="uber-walking-badge">
        <span>🚶</span>
        <span>${match.walkTimeMin} min</span>
        <span>•</span>
        <span>${match.walkDistanceM} m</span>
      </div>
    `,
    iconSize: [130, 28],
    iconAnchor: [65, 14]
  });

  AppState.uberWalkingBadgeMarker = L.marker(midPoint, { icon: badgeIcon }).addTo(AppState.uberMapInstance);

  // Enquadra a visão do mapa com foco no usuário e no salão
  const bounds = L.latLngBounds([userCoords, salonCoords]);
  if (shouldFly) {
    AppState.uberMapInstance.flyToBounds(bounds, {
      paddingTopLeft: [50, 40],
      paddingBottomRight: [50, 240], // compensa a gaveta inferior
      duration: 0.7
    });
  } else {
    AppState.uberMapInstance.fitBounds(bounds, {
      paddingTopLeft: [50, 40],
      paddingBottomRight: [50, 240]
    });
  }

  trackEvent('uber_match_selected', {
    salon_id: match.salon.id,
    service_id: match.service.id,
    walk_min: match.walkTimeMin,
    discount_pct: match.pricing.discountPct
  });
}

function recenterUberMap() {
  if (!AppState.uberMapInstance) return;
  const userCoords = [-23.5650, -46.6810];
  if (AppState.selectedDemandMatch) {
    const salonCoords = [AppState.selectedDemandMatch.salon.lat, AppState.selectedDemandMatch.salon.lng];
    const bounds = L.latLngBounds([userCoords, salonCoords]);
    AppState.uberMapInstance.flyToBounds(bounds, {
      paddingTopLeft: [50, 40],
      paddingBottomRight: [50, 240],
      duration: 0.8
    });
  } else {
    AppState.uberMapInstance.flyTo(userCoords, 15, { duration: 0.8 });
  }
}

function confirmUberDemandBooking() {
  const match = AppState.selectedDemandMatch || (AppState.uberMatchesList && AppState.uberMatchesList[0]);
  if (!match) {
    showToast('Selecione uma opção de salão antes de confirmar.');
    return;
  }

  // Congela o estado para o Checkout
  AppState.selectedSalon = match.salon;
  AppState.selectedService = match.service;
  AppState.currentPricing = match.pricing;
  AppState.lastWalkTimeMin = match.walkTimeMin;
  AppState.lastWalkDistanceM = match.walkDistanceM;
  AppState.selectedStaff = {
    id: 'any',
    name: 'Qualquer Profissional Disponível',
    role: 'Cadeira Ociosa (Encaixe Imediato)',
    isAny: true
  };
  AppState.selectedTime = '14:00';

  showToast(`Match confirmado com ${match.salon.name}! Abrindo checkout...`);
  trackEvent('demand_booking_proceed_checkout', {
    salon_id: match.salon.id,
    service_id: match.service.id,
    price_final: match.pricing.finalPrice,
    walk_min: match.walkTimeMin
  });

  navigateTo('checkout');
}

// --- SCREEN 3: SALON DETAIL & RADIAL TIME GAUGE ---

// Renderiza estrelas de avaliação via SVG (sem emojis, Seção 12.9)
function renderStars(rating) {
  let html = '';
  for (let i = 1; i <= 5; i++) {
    if (i <= rating) {
      html += `<svg width="11" height="11" viewBox="0 0 24 24" fill="var(--star-gold)" stroke="var(--star-gold)" stroke-width="1"><polygon points="12 2 15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9.27 8.91 8.26 12 2"/></svg>`;
    } else {
      html += `<svg width="11" height="11" viewBox="0 0 24 24" fill="none" stroke="#D1D5DB" stroke-width="1.5"><polygon points="12 2 15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9.27 8.91 8.26 12 2"/></svg>`;
    }
  }
  return html;
}

let currentDetailServiceFilter = 'all';

function renderDetailServices(salon) {
  const container = document.getElementById('detail-services-list');
  const countEl = document.getElementById('detail-service-count');
  const chipsContainer = document.getElementById('service-category-chips');
  if (!container || !salon.services) return;

  if (countEl) {
    countEl.textContent = `${salon.services.length} opções disponíveis`;
  }

  // Mapa de nomes legíveis de categorias
  const categoryLabels = {
    all: 'Todos',
    hair: 'Cabelo',
    nails: 'Unhas',
    barber: 'Barba',
    depilation: 'Depilação',
    esthetic: 'Estética',
    massage: 'Massagem'
  };

  // Coleta categorias únicas presentes nos serviços deste salão
  const presentCategories = ['all'];
  salon.services.forEach(srv => {
    if (srv.category && !presentCategories.includes(srv.category)) {
      presentCategories.push(srv.category);
    }
  });

  // Renderiza chips de categoria
  if (chipsContainer) {
    chipsContainer.innerHTML = '';
    presentCategories.forEach(cat => {
      const chip = document.createElement('button');
      chip.type = 'button';
      chip.className = `service-cat-chip ${currentDetailServiceFilter === cat ? 'active' : ''}`;
      chip.textContent = categoryLabels[cat] || cat;
      chip.onclick = () => {
        currentDetailServiceFilter = cat;
        renderDetailServices(salon);
      };
      chipsContainer.appendChild(chip);
    });
  }

  // Filtra serviços
  const filteredServices = currentDetailServiceFilter === 'all'
    ? salon.services
    : salon.services.filter(s => s.category === currentDetailServiceFilter);

  container.innerHTML = '';
  filteredServices.forEach(srv => {
    const isSelected = AppState.selectedService && AppState.selectedService.id === srv.id;
    const card = document.createElement('div');
    card.className = `service-item-card ${isSelected ? 'active' : ''}`;
    card.id = `service-card-${srv.id}`;
    card.onclick = () => selectService(srv.id);

    card.innerHTML = `
      <div class="service-info-left">
        <h4 class="service-item-name">${srv.name}</h4>
        <p class="service-item-desc">${srv.description}</p>
        <div class="service-item-meta">
          <span class="service-item-tag">${categoryLabels[srv.category] || srv.category}</span>
          <span class="service-item-duration">
            <svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="10"/><polyline points="12 6 12 12 16 14"/></svg>
            ${srv.durationMinutes} min
          </span>
        </div>
      </div>
      <div class="service-info-right">
        <span class="service-item-price">R$ ${srv.basePrice.toFixed(2)}</span>
        <div class="service-item-radio">
          <div class="service-item-radio-inner"></div>
        </div>
      </div>
    `;
    container.appendChild(card);
  });
}

function selectService(serviceId) {
  const salon = AppState.selectedSalon;
  if (!salon || !salon.services) return;
  const srv = salon.services.find(s => s.id === serviceId);
  if (!srv) return;

  AppState.selectedService = srv;

  // Atualiza Hero Card
  const titleEl = document.getElementById('detail-service-title');
  const descEl = document.getElementById('detail-service-desc');
  if (titleEl) titleEl.textContent = srv.name;
  if (descEl) descEl.textContent = `${srv.durationMinutes} min • ${srv.description}`;

  // Atualiza destaque visual dos cards
  document.querySelectorAll('.service-item-card').forEach(c => c.classList.remove('active'));
  const activeCard = document.getElementById(`service-card-${srv.id}`);
  if (activeCard) activeCard.classList.add('active');

  // Recalcula limites do slider (duração + buffer 10 min)
  const bufferMinutes = 10;
  const totalServiceTime = srv.durationMinutes + bufferMinutes;
  const slotsUsed = Math.ceil(totalServiceTime / 15);
  const maxSlotIndex = Math.max(0, TIME_SLOTS.length - 1 - slotsUsed);

  const slider = document.getElementById('clock-time-slider');
  if (slider) {
    slider.max = maxSlotIndex;
    if (AppState.selectedTimeSlotIndex > maxSlotIndex) {
      AppState.selectedTimeSlotIndex = maxSlotIndex;
    }
    slider.value = AppState.selectedTimeSlotIndex;
  }

  // Atualiza Mostrador Radial e preço recalculado
  updateRadialClock();
}

function renderDetailScreen() {
  const salon = AppState.selectedSalon;
  if (!salon) return;

  const activeService = AppState.selectedService || (salon.services && salon.services[0]) || salon.service;
  if (!AppState.selectedService) {
    AppState.selectedService = activeService;
  }

  document.getElementById('detail-hero-img').src = salon.image;
  document.getElementById('detail-salon-name').textContent = salon.name;
  document.getElementById('detail-salon-address').textContent = salon.address;
  document.getElementById('detail-salon-rating').textContent = `${salon.rating} (${salon.reviewsCount} avaliações)`;
  document.getElementById('detail-service-title').textContent = activeService.name;
  document.getElementById('detail-service-desc').textContent = `${activeService.durationMinutes} min • ${activeService.description}`;

  // Renderiza Catálogo de Serviços do Salão (Seção 6.1 e 6.3)
  renderDetailServices(salon);

  // Renderiza Equipe (com opção de Profissional Neutro / Qualquer Profissional)
  const staffCarousel = document.getElementById('staff-carousel-container');
  staffCarousel.innerHTML = '';

  // 1. Card "Qualquer Profissional Disponível" (Opção padrão neutra)
  const isAnySelected = !AppState.selectedStaff || AppState.selectedStaff.id === 'any' || AppState.selectedStaff.isAny;
  const anyCard = document.createElement('div');
  anyCard.className = `staff-card ${isAnySelected ? 'active' : ''}`;
  anyCard.onclick = () => {
    AppState.selectedStaff = { id: 'any', name: 'Qualquer Profissional', role: 'Primeiro disponível', isAny: true };
    renderDetailScreen();
  };
  anyCard.innerHTML = `
    <div class="staff-avatar-any">
      <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"/><circle cx="9" cy="7" r="4"/><path d="M23 21v-2a4 4 0 0 0-3-3.87"/><path d="M16 3.13a4 4 0 0 1 0 7.75"/></svg>
    </div>
    <span class="staff-name">Qualquer um</span>
    <span class="staff-role">Disponível</span>
  `;
  staffCarousel.appendChild(anyCard);

  // 2. Membros da equipe do salão
  salon.staff.forEach((st, idx) => {
    const isSelected = AppState.selectedStaff && AppState.selectedStaff.id === st.id;
    const staffCard = document.createElement('div');
    staffCard.className = `staff-card ${isSelected ? 'active' : ''}`;
    staffCard.onclick = () => {
      AppState.selectedStaff = st;
      renderDetailScreen();
    };
    staffCard.innerHTML = `
      <img class="staff-avatar" src="${st.avatar}" alt="${st.name}">
      <span class="staff-name">${st.name}</span>
      <span class="staff-role">${st.role}</span>
    `;
    staffCarousel.appendChild(staffCard);
  });

  // Renderiza Avaliações Recentes (Seção 12.9 — leitura de seed)
  const reviewsContainer = document.getElementById('detail-reviews-container');
  if (reviewsContainer) {
    reviewsContainer.innerHTML = '';
    if (salon.reviews && salon.reviews.length > 0) {
      const header = document.createElement('div');
      header.className = 'section-header';
      header.style.cssText = 'padding:0 0 8px;';
      header.innerHTML = `
        <span class="section-title" style="font-size:13px;">Avaliações</span>
        <span style="font-size:11px; font-weight:700; color:var(--neutral-muted);">${salon.reviewsCount} avaliações</span>
      `;
      reviewsContainer.appendChild(header);
      salon.reviews.slice(0, 3).forEach(review => {
        const reviewEl = document.createElement('div');
        reviewEl.className = 'review-card';
        reviewEl.innerHTML = `
          <div class="review-header">
            <div class="review-author">${review.author}</div>
            <div class="review-stars-row">${renderStars(review.rating)}</div>
          </div>
          <p class="review-comment">"${review.comment}"</p>
          <span class="review-date">${review.date}</span>
        `;
        reviewsContainer.appendChild(reviewEl);
      });
    }
  }

  // Renderiza Faixa Dinâmica de Datas (Bloco B4)
  renderDateStrip();

  // Verifica disponibilidade de slots na data selecionada (Domingo = Fechado, Seção 6.4)
  const isSunday = getDayOfWeek(AppState.selectedDate) === 0;
  const emptyBanner = document.getElementById('detail-empty-slots-banner');
  const clockContainer = document.querySelector('.radial-clock-container');
  const quickSlotsSection = document.getElementById('detail-time-strip-section');
  const checkoutBtn = document.querySelector('#screen-detail .card-action-btn');

  if (isSunday) {
    if (emptyBanner) {
      emptyBanner.style.display = 'block';
      emptyBanner.innerHTML = `
        <div class="empty-slots-warning">
          <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="#D97706" stroke-width="2"><circle cx="12" cy="12" r="10"/><line x1="12" y1="8" x2="12" y2="12"/><line x1="12" y1="16" x2="12.01" y2="16"/></svg>
          <div class="empty-slots-title">Nenhum horário com desconto nesta data</div>
          <div class="empty-slots-desc">Este salão está fechado aos domingos ou com todos os slots preenchidos. Veja horários no próximo dia útil.</div>
          <button type="button" class="empty-slots-action-btn" onclick="selectNextAvailableDate()">Ver Segunda-feira</button>
        </div>
      `;
    }
    if (clockContainer) clockContainer.style.display = 'none';
    if (quickSlotsSection) quickSlotsSection.style.display = 'none';
    if (checkoutBtn) checkoutBtn.parentElement.style.display = 'none';
    return;
  } else {
    if (emptyBanner) emptyBanner.style.display = 'none';
    if (clockContainer) clockContainer.style.display = 'flex';
    if (quickSlotsSection) quickSlotsSection.style.display = 'block';
    if (checkoutBtn) checkoutBtn.parentElement.style.display = 'block';
  }

  // Renderiza a Tira de Horários do Dia (Quick Slots com ocupação e descontos)
  renderHorizontalTimeStrip(salon, AppState.selectedDate);

  // Ajusta slider para respeitar duração do serviço selecionado + buffer de 10 min (Seção 7.1)
  const bufferMinutes = 10;
  const totalServiceTime = activeService.durationMinutes + bufferMinutes;
  const slotsUsed = Math.ceil(totalServiceTime / 15);
  const maxSlotIndex = Math.max(0, TIME_SLOTS.length - 1 - slotsUsed);

  const slider = document.getElementById('clock-time-slider');
  if (slider) {
    slider.max = maxSlotIndex;
    if (AppState.selectedTimeSlotIndex > maxSlotIndex) {
      AppState.selectedTimeSlotIndex = maxSlotIndex;
    }
    slider.value = AppState.selectedTimeSlotIndex;
  }

  // Inicializa Mostrador Radial
  updateRadialClock();
}

function renderDateStrip() {
  const container = document.getElementById('detail-date-strip-row');
  if (!container) return;

  const dates = [
    { dateStr: '2026-09-26', dayAbbr: 'Sex', dayNum: '26', dow: 5 },
    { dateStr: '2026-09-27', dayAbbr: 'Sáb', dayNum: '27', dow: 6 },
    { dateStr: '2026-09-28', dayAbbr: 'Dom', dayNum: '28', dow: 0 },
    { dateStr: '2026-09-29', dayAbbr: 'Seg', dayNum: '29', dow: 1 },
    { dateStr: '2026-09-30', dayAbbr: 'Ter', dayNum: '30', dow: 2 }
  ];

  container.innerHTML = '';
  dates.forEach(d => {
    const isActive = AppState.selectedDate === d.dateStr;
    const pill = document.createElement('div');
    pill.className = `date-day-pill ${isActive ? 'active' : ''}`;
    pill.onclick = () => selectDetailDate(d.dateStr, pill);
    pill.innerHTML = `
      <span class="day-abbr">${d.dayAbbr}</span>
      <span class="day-num">${d.dayNum}</span>
    `;
    container.appendChild(pill);
  });
}

function renderHorizontalTimeStrip(salon, dateStr) {
  const container = document.getElementById('detail-quick-slots-container');
  const summaryEl = document.getElementById('detail-slots-summary-label');
  if (!container) return;

  const keyTimes = [
    '09:00', '09:30', '10:00', '10:30', '11:00', '11:30',
    '12:00', '12:30', '13:00', '13:30', '14:00', '14:30',
    '15:00', '15:30', '16:00', '16:30', '17:00', '17:30',
    '18:00', '18:30'
  ];

  let discountCount = 0;
  let occupiedCount = 0;

  container.innerHTML = '';
  keyTimes.forEach(t => {
    const pricing = calculateSlotPrice(salon, t, AppState.selectedService, dateStr);
    const isSelected = AppState.selectedTime === t;

    if (pricing.isOccupied) occupiedCount++;
    if (pricing.hasDiscount) discountCount++;

    const pill = document.createElement('div');
    let classes = ['quick-slot-pill'];
    if (pricing.isOccupied) classes.push('occupied');
    else if (pricing.hasDiscount) {
      classes.push('discount');
      if (pricing.type === 'urgent') classes.push('urgent-deal');
    }
    if (isSelected && !pricing.isOccupied) classes.push('active');

    pill.className = classes.join(' ');

    let badgeText = 'Livre';
    if (pricing.isOccupied) {
      badgeText = 'Ocupado';
    } else if (pricing.hasDiscount) {
      badgeText = `-${pricing.discountPct}%`;
    }

    pill.innerHTML = `
      <span class="slot-time-text">${t}</span>
      <span class="slot-badge-sub">${badgeText}</span>
    `;

    pill.onclick = () => {
      if (pricing.isOccupied) {
        showToast('Este horário está ocupado. Por favor, escolha um slot disponível.');
        return;
      }
      const idx = TIME_SLOTS.indexOf(t);
      if (idx !== -1) {
        AppState.selectedTimeSlotIndex = idx;
        const slider = document.getElementById('clock-time-slider');
        if (slider) slider.value = idx;
        updateRadialClock();
        renderHorizontalTimeStrip(salon, dateStr);
      }
    };

    container.appendChild(pill);
  });

  if (summaryEl) {
    summaryEl.textContent = `${discountCount} com desconto • ${occupiedCount} ocupados`;
  }
}

function selectDetailDate(dateStr, element) {
  AppState.selectedDate = dateStr;
  document.querySelectorAll('#detail-date-strip-row .date-day-pill').forEach(pill => pill.classList.remove('active'));
  if (element) element.classList.add('active');
  renderDetailScreen();
}

function selectNextAvailableDate() {
  selectDetailDate('2026-09-29');
}

function updateRadialClock() {
  const slotIndex = AppState.selectedTimeSlotIndex;
  const time = TIME_SLOTS[slotIndex];
  AppState.selectedTime = time;

  // Atualiza exibição de texto
  document.getElementById('clock-time-val').textContent = time;

  // Atualiza AM / PM visual
  const hour = parseInt(time.split(':')[0], 10);
  const isPM = hour >= 12;
  document.getElementById('btn-period-am').classList.toggle('active', !isPM);
  document.getElementById('btn-period-pm').classList.toggle('active', isPM);

  // Calcula Preço Dinâmico e Ocupação para este horário
  const pricing = calculateSlotPrice(AppState.selectedSalon, time, AppState.selectedService, AppState.selectedDate);
  AppState.currentPricing = pricing;

  // Atualiza Gauge SVG Arc
  const totalSlots = TIME_SLOTS.length - 1;
  const progress = slotIndex / totalSlots;
  const maxDash = 283; // Comprimento do arco semi-circular
  const currentDash = progress * maxDash;

  const fillArc = document.getElementById('gauge-fill-arc');
  if (fillArc) {
    fillArc.style.strokeDasharray = `${currentDash} ${maxDash}`;
    fillArc.classList.toggle('discount', pricing.hasDiscount && !pricing.isOccupied);
    if (pricing.isOccupied) {
      fillArc.style.stroke = '#94A3B8';
    } else if (pricing.hasDiscount) {
      fillArc.style.stroke = pricing.type === 'urgent' ? 'var(--promo-coral)' : '#0E8A73';
    } else {
      fillArc.style.stroke = 'var(--primary)';
    }
  }

  // Atualiza Card de Preço do Horário
  const slotCard = document.getElementById('slot-pricing-summary');
  const checkoutBtn = document.querySelector('#screen-detail .card-action-btn');

  if (slotCard) {
    if (pricing.isOccupied) {
      slotCard.innerHTML = `
        <div>
          <div style="font-size:11px; font-weight:700; color:#EF4444;">
            Horário Selecionado: <strong>${time}</strong> (Indisponível)
          </div>
          <div style="font-size:13px; font-weight:700; color:var(--neutral-text); margin-top:3px;">
            Horário Já Preenchido
          </div>
          <div style="font-size:10px; color:var(--neutral-muted); margin-top:2px;">
            ${pricing.subtext}
          </div>
        </div>
        <div>
          <span style="font-size:10px; font-weight:700; padding:3px 8px; border-radius:4px; background:#FEE2E2; color:#B91C1C;">
            Ocupado
          </span>
        </div>
      `;
      if (checkoutBtn) {
        checkoutBtn.disabled = true;
        checkoutBtn.style.opacity = '0.55';
        checkoutBtn.style.cursor = 'not-allowed';
        const label = checkoutBtn.querySelector('span');
        if (label) label.textContent = 'Horário Ocupado - Escolha Outro';
      }
    } else {
      slotCard.innerHTML = `
        <div>
          <div style="font-size:11px; font-weight:600; color:var(--neutral-muted);">
            Horário Selecionado: <strong>${time}</strong>
          </div>
          <div class="price-values-row" style="margin-top:2px;">
            ${pricing.hasDiscount ? `<span class="price-base-slashed">R$ ${pricing.basePrice.toFixed(2)}</span>` : ''}
            <span class="price-final-bold">R$ ${pricing.finalPrice.toFixed(2)}</span>
            ${pricing.hasDiscount ? `<span class="discount-tag">-${pricing.discountPct}%</span>` : ''}
          </div>
          <div style="font-size:10px; color:var(--neutral-muted); margin-top:2px;">
            ${pricing.subtext}
          </div>
        </div>
        <div>
          ${pricing.hasDiscount ? `
            <span class="badge-tag-dynamic ${pricing.type === 'urgent' ? 'urgent' : ''}">
              ${pricing.badgeText}
            </span>
          ` : `<span style="font-size:11px; font-weight:700; color:var(--neutral-muted);">Padrão</span>`}
        </div>
      `;
      if (checkoutBtn) {
        checkoutBtn.disabled = false;
        checkoutBtn.style.opacity = '1';
        checkoutBtn.style.cursor = 'pointer';
        const label = checkoutBtn.querySelector('span');
        if (label) label.textContent = 'Avançar para Pagamento';
      }
    }
  }

  // Calcula horas até o início do slot (Seção 11 da spec)
  const [slotH, slotM] = time.split(':').map(Number);
  const now = new Date();
  const slotDate = new Date();
  slotDate.setHours(slotH, slotM, 0, 0);
  let hoursUntil = (slotDate - now) / 3600000;
  if (hoursUntil < 0) hoursUntil += 24;
  hoursUntil = Math.round(hoursUntil * 10) / 10;

  const currentService = AppState.selectedService || (AppState.selectedSalon.services && AppState.selectedSalon.services[0]) || AppState.selectedSalon.service;

  trackEvent('slot_viewed', {
    merchant_id: AppState.selectedSalon.id,
    service_id: currentService.id,
    start_time: time,
    has_discount: pricing.hasDiscount,
    discount_pct: pricing.discountPct,
    hours_until: hoursUntil
  });
}

function onSliderTimeChange(val) {
  AppState.selectedTimeSlotIndex = parseInt(val, 10);
  updateRadialClock();
}

function proceedToCheckout() {
  const currentService = AppState.selectedService || (AppState.selectedSalon.services && AppState.selectedSalon.services[0]) || AppState.selectedSalon.service;
  trackEvent('slot_selected', {
    merchant_id: AppState.selectedSalon.id,
    service_id: currentService.id,
    start_time: AppState.selectedTime,
    price_final: AppState.currentPricing.finalPrice
  });
  navigateTo('checkout');
}

// --- SCREEN 4: CHECKOUT & LUHN VALIDATION ---
function renderCheckoutScreen() {
  const salon = AppState.selectedSalon;
  const pricing = AppState.currentPricing;
  const currentService = AppState.selectedService || (salon.services && salon.services[0]) || salon.service;

  document.getElementById('checkout-salon-name').textContent = salon.name;
  document.getElementById('checkout-service-name').textContent = currentService.name;
  document.getElementById('checkout-staff-name').textContent = AppState.selectedStaff ? AppState.selectedStaff.name : 'Qualquer profissional disponível';
  document.getElementById('checkout-datetime').textContent = `Hoje, 26 de Setembro às ${AppState.selectedTime}`;

  document.getElementById('checkout-base-price').textContent = `R$ ${pricing.basePrice.toFixed(2)}`;
  document.getElementById('checkout-discount-row').style.display = pricing.hasDiscount ? 'flex' : 'none';
  document.getElementById('checkout-discount-val').textContent = `- R$ ${(pricing.basePrice - pricing.finalPrice).toFixed(2)} (${pricing.discountPct}%)`;
  document.getElementById('checkout-final-total').textContent = `R$ ${pricing.finalPrice.toFixed(2)}`;
}

// Algoritmo de Luhn Local (Não envia dados, apenas valida)
function validateLuhn(cardNumber) {
  const cleanNum = cardNumber.replace(/\D/g, '');
  if (cleanNum.length < 13 || cleanNum.length > 19) return false;

  let sum = 0;
  let alternate = false;
  for (let i = cleanNum.length - 1; i >= 0; i--) {
    let n = parseInt(cleanNum.charAt(i), 10);
    if (alternate) {
      n *= 2;
      if (n > 9) n = (n % 10) + 1;
    }
    sum += n;
    alternate = !alternate;
  }
  return (sum % 10 === 0);
}

function onCardNumberInput(input) {
  // Máscara 0000 0000 0000 0000
  let val = input.value.replace(/\D/g, '').substring(0, 16);
  let formatted = val.match(/.{1,4}/g)?.join(' ') || val;
  input.value = formatted;

  const isValid = validateLuhn(val);
  AppState.isCardValid = isValid;

  input.classList.toggle('is-valid', isValid);
  input.classList.toggle('is-invalid', val.length >= 13 && !isValid);

  if (isValid) {
    trackEvent('card_validated', { last_4: val.slice(-4) });
  }
}

function startReservationTimer() {
  if (AppState.timerIntervalId) clearInterval(AppState.timerIntervalId);
  AppState.reservationTimerSeconds = 600; // 10 min

  function tick() {
    AppState.reservationTimerSeconds--;
    if (AppState.reservationTimerSeconds <= 0) {
      clearInterval(AppState.timerIntervalId);
      alert('Seu tempo de reserva de 10 minutos expirou. O horário foi liberado.');
      trackEvent('checkout_abandoned', { step: 'expiry', reason: 'timer_expired' });
      navigateTo('detail');
      return;
    }
    const mins = String(Math.floor(AppState.reservationTimerSeconds / 60)).padStart(2, '0');
    const secs = String(AppState.reservationTimerSeconds % 60).padStart(2, '0');
    const timerElem = document.getElementById('checkout-timer-countdown');
    if (timerElem) timerElem.textContent = `${mins}:${secs}`;
  }

  tick();
  AppState.timerIntervalId = setInterval(tick, 1000);
}

// --- SELETOR DE MÉTODO DE PAGAMENTO (CARTÃO VS PIX) ---
function selectPaymentMethod(method) {
  AppState.selectedPaymentMethod = method;
  trackEvent('payment_method_selected', { method });

  const tabCard = document.getElementById('tab-payment-card');
  const tabPix = document.getElementById('tab-payment-pix');
  const cardForm = document.getElementById('checkout-card-form');
  const pixContainer = document.getElementById('checkout-pix-container');
  const btnCheckoutLabel = document.getElementById('btn-checkout-label');

  if (method === 'pix') {
    tabCard?.classList.remove('active');
    tabPix?.classList.add('active');
    if (cardForm) cardForm.style.display = 'none';
    if (pixContainer) pixContainer.style.display = 'block';
    if (btnCheckoutLabel) btnCheckoutLabel.textContent = 'Confirmar Pagamento Simulado via Pix';
  } else {
    tabPix?.classList.remove('active');
    tabCard?.classList.add('active');
    if (cardForm) cardForm.style.display = 'block';
    if (pixContainer) pixContainer.style.display = 'none';
    if (btnCheckoutLabel) btnCheckoutLabel.textContent = 'Confirmar Agendamento';
  }
}

function copyPixCode() {
  const pixVal = document.getElementById('pix-copy-paste-val')?.textContent || '';
  if (navigator.clipboard && navigator.clipboard.writeText) {
    navigator.clipboard.writeText(pixVal);
  }
  const btnLabel = document.getElementById('pix-btn-copy-label');
  if (btnLabel) {
    btnLabel.textContent = 'Copiado!';
    setTimeout(() => { btnLabel.textContent = 'Copiar'; }, 2000);
  }
  showToast('Chave Pix Copia e Cola copiada para a área de transferência!');
}

function confirmBooking() {
  if (AppState.selectedPaymentMethod === 'card') {
    const cardInput = document.getElementById('checkout-card-input');
    const cardVal = cardInput ? cardInput.value.replace(/\D/g, '') : '';
    if (!validateLuhn(cardVal)) {
      showToast('Por favor, informe uma numeração válida de cartão (Algoritmo de Luhn).');
      if (cardInput) cardInput.focus();
      return;
    }
  }

  const btn = document.getElementById('btn-confirm-checkout');
  btn.disabled = true;
  btn.innerHTML = `<span class="spinner-sm"></span> Processando agendamento seguro...`;

  // Simula latência de 1.4s (Mock de Validação)
  setTimeout(() => {
    btn.disabled = false;
    const btnCheckoutLabel = document.getElementById('btn-checkout-label');
    if (btnCheckoutLabel) {
      btnCheckoutLabel.textContent = AppState.selectedPaymentMethod === 'pix' ? 'Confirmar Pagamento Simulado via Pix' : 'Confirmar Agendamento';
    }

    const currentService = AppState.selectedService || (AppState.selectedSalon.services && AppState.selectedSalon.services[0]) || AppState.selectedSalon.service;
    const currentStaff = AppState.selectedStaff || { id: 'any', name: 'Qualquer Profissional', role: 'Primeiro disponível', isAny: true };

    // Congela Snapshot de Preço e Reserva
    const newAppointment = {
      id: 'BP-' + Math.floor(100000 + Math.random() * 900000),
      salon: AppState.selectedSalon,
      service: currentService,
      staff: currentStaff,
      time: AppState.selectedTime,
      date: 'Hoje, 26 de Set',
      pricing: AppState.currentPricing,
      paymentMethod: AppState.selectedPaymentMethod,
      status: 'CONFIRMED',
      bookedAt: new Date().toISOString(),
      walkingTimeMin: AppState.lastWalkTimeMin || (AppState.selectedSalon.distanceKm ? Math.round(AppState.selectedSalon.distanceKm * 12) : null),
      walkingDistanceM: AppState.lastWalkDistanceM || (AppState.selectedSalon.distanceKm ? Math.round(AppState.selectedSalon.distanceKm * 1000) : null)
    };

    AppState.confirmedAppointment = newAppointment;
    // Adiciona na frente da lista de agendamentos para a tela Meus Agendamentos
    AppState.appointmentsList.unshift(newAppointment);

    emitAppointmentStatusChanged(newAppointment.id, 'PENDING_PAYMENT', 'CONFIRMED');

    trackEvent('checkout_completed', {
      appointment_id: AppState.confirmedAppointment.id,
      total: AppState.currentPricing.finalPrice,
      payment_method: AppState.selectedPaymentMethod,
      discount_applied: AppState.currentPricing.hasDiscount ? AppState.currentPricing.discountPct : 0
    });

    if (AppState.timerIntervalId) clearInterval(AppState.timerIntervalId);
    showToast('Agendamento confirmado com sucesso!');
    navigateTo('confirm');
  }, 1400);
}

// --- SCREEN 5: CONFIRMATION & MEUS AGENDAMENTOS ---
function renderConfirmScreen() {
  const appt = AppState.confirmedAppointment;
  if (!appt) return;

  document.getElementById('confirm-voucher-code').textContent = appt.id;
  document.getElementById('confirm-salon-name').textContent = appt.salon.name;
  
  const addressEl = document.getElementById('confirm-address');
  if (addressEl) {
    if (appt.walkingTimeMin) {
      addressEl.innerHTML = `${appt.salon.address}<br><span style="display:inline-flex; align-items:center; gap:5px; margin-top:5px; color:var(--primary); font-weight:800; font-size:11px;">🚶 Trajeto a pé: ${appt.walkingTimeMin} min (${appt.walkingDistanceM} m)</span>`;
    } else {
      addressEl.textContent = appt.salon.address;
    }
  }

  document.getElementById('confirm-service-name').textContent = appt.service.name;
  document.getElementById('confirm-staff').textContent = appt.staff ? appt.staff.name : 'Qualquer Profissional Disponível';
  document.getElementById('confirm-time').textContent = `Hoje às ${appt.time}`;
  document.getElementById('confirm-total-paid').textContent = `R$ ${appt.pricing.finalPrice.toFixed(2)}`;
}

// --- MODAL DE CANCELAMENTO (T4 da Spec) ---
function openCancelModal() {
  document.getElementById('cancel-modal-overlay').classList.add('active');
}

function closeCancelModal() {
  document.getElementById('cancel-modal-overlay').classList.remove('active');
  cancelingApptId = null;
}

let selectedCancelReason = 'Mudei de planos';
function selectCancelReason(btn, reason) {
  document.querySelectorAll('.reason-option-btn').forEach(b => b.classList.remove('selected'));
  btn.classList.add('selected');
  selectedCancelReason = reason;
  // Exibir campo de texto livre quando "Outro" for selecionado (Seção 6.2)
  const outroField = document.getElementById('cancel-outro-field');
  if (outroField) {
    outroField.style.display = reason === 'Outro' ? 'block' : 'none';
  }
}

function executeCancellation() {
  let finalReason = selectedCancelReason;
  if (selectedCancelReason === 'Outro') {
    const outroText = (document.getElementById('cancel-outro-text')?.value || '').trim();
    if (outroText) finalReason = `Outro: ${outroText}`;
  }

  // Identifica qual agendamento está sendo cancelado
  let targetAppt = AppState.confirmedAppointment;
  if (cancelingApptId) {
    const found = AppState.appointmentsList.find(a => a.id === cancelingApptId);
    if (found) targetAppt = found;
  }

  if (targetAppt) {
    const prevStatus = targetAppt.status;
    targetAppt.status = 'CANCELLED_BY_USER';
    targetAppt.cancellationReason = finalReason;

    emitAppointmentStatusChanged(targetAppt.id, prevStatus, 'CANCELLED_BY_USER', { reason: finalReason });

    trackEvent('cancellation_completed', {
      appointment_id: targetAppt.id,
      reason: finalReason
    });
  }

  closeCancelModal();
  showToast('Seu agendamento foi cancelado com sucesso.');
  if (AppState.currentScreen === 'appointments') {
    renderAppointmentsScreen();
  } else {
    navigateTo('appointments');
  }
}

// Tooltip "Por que o preço varia?"
function openPriceHelpModal() {
  trackEvent('price_help_tooltip_opened', { merchant_id: AppState.selectedSalon.id });
  document.getElementById('price-help-modal-overlay').classList.add('active');
}

function closePriceHelpModal() {
  document.getElementById('price-help-modal-overlay').classList.remove('active');
}

// --- SCREEN 6: PERFIL, LGPD E EXPORTAÇÃO ---
function exportAnalyticsData() {
  const storedEvents = JSON.parse(localStorage.getItem('bp_analytics_events') || '[]');
  const allEvents = storedEvents.length > 0 ? storedEvents : AppState.analyticsEvents;

  const exportPayload = {
    exportedAt: new Date().toISOString(),
    sessionUser: AppState.userSession,
    totalEvents: allEvents.length,
    events: allEvents
  };

  const dataStr = "data:text/json;charset=utf-8," + encodeURIComponent(JSON.stringify(exportPayload, null, 2));
  const downloadAnchor = document.createElement('a');
  downloadAnchor.setAttribute("href", dataStr);
  downloadAnchor.setAttribute("download", `beautypass_analytics_${Date.now()}.json`);
  document.body.appendChild(downloadAnchor);
  downloadAnchor.click();
  downloadAnchor.remove();

  showToast('Relatório de métricas exportado em JSON!');
}

function openDeleteAccountModal() {
  document.getElementById('delete-modal-overlay')?.classList.add('active');
}

function closeDeleteAccountModal() {
  document.getElementById('delete-modal-overlay')?.classList.remove('active');
}

function executeDeleteAccount() {
  closeDeleteAccountModal();
  localStorage.removeItem('bp_user_session');
  localStorage.removeItem('bp_analytics_events');
  AppState.userSession = null;
  AppState.analyticsEvents = [];
  AppState.confirmedAppointment = null;
  showToast('Conta e dados excluídos com sucesso (LGPD).');
  navigateTo('onboarding');
}

// ===================================================================
// 8. MOTOR DE MATCH INTELIGENTE & TARIFA JUSTA (ON-DEMAND HAIRCUT)
// ===================================================================
function selectTimeWindow(btn, windowKey) {
  document.querySelectorAll('#smart-time-windows .window-chip').forEach(b => b.classList.remove('active'));
  if (btn) btn.classList.add('active');
  AppState.smartMatchWindow = windowKey;
  updateLivePricingEstimator();
}

function selectSmartService(serviceType, btn) {
  document.querySelectorAll('#smart-service-pills .smart-icon-pill').forEach(b => b.classList.remove('active'));
  if (btn) btn.classList.add('active');
  AppState.smartSelectedServiceType = serviceType;
  updateLivePricingEstimator();
}

function selectSmartRadius(radiusKm, btn) {
  document.querySelectorAll('#smart-radius-pills .smart-radius-pill').forEach(b => b.classList.remove('active'));
  if (btn) btn.classList.add('active');
  AppState.smartSelectedRadius = parseFloat(radiusKm);
  updateLivePricingEstimator();
}

// Estimômetro Dinâmico em Tempo Real (Reage instantaneamente aos cliques)
function updateLivePricingEstimator() {
  const windowKey = AppState.smartMatchWindow || 'now_2h';
  const serviceType = AppState.smartSelectedServiceType || 'hair_cut_fem';

  // Preço base de referência
  let basePriceRef = 120;
  if (serviceType === 'hair_cut_fem') basePriceRef = 120;
  else if (serviceType === 'hair_cut_masc') basePriceRef = 75;
  else if (serviceType === 'hair_treatment') basePriceRef = 145;

  let occupancyPct = 72;
  let demandLevel = 'low';
  let demandText = 'Demanda Baixa na Região';
  let discountPct = 35;

  if (windowKey === 'now_2h') {
    occupancyPct = 72;
    demandLevel = 'low';
    demandText = 'Demanda Baixa na Região';
    discountPct = 35;
  } else if (windowKey === 'today_afternoon') {
    occupancyPct = 65;
    demandLevel = 'low';
    demandText = 'Ociosidade da Tarde';
    discountPct = 30;
  } else if (windowKey === 'today_evening') {
    occupancyPct = 34;
    demandLevel = 'high';
    demandText = 'Horário Nobre (Alta Procura)';
    discountPct = 15;
  } else if (windowKey === 'tomorrow_morning') {
    occupancyPct = 58;
    demandLevel = 'medium';
    demandText = 'Demanda Moderada';
    discountPct = 25;
  }

  const estPrice = Math.round(basePriceRef * (1 - discountPct / 100));

  // Atualiza elementos do DOM com microinterações
  const demandLabel = document.getElementById('estimator-demand-label');
  const demandDot = document.getElementById('demand-dot-indicator');
  const priceVal = document.getElementById('estimator-price-val');
  const barFill = document.getElementById('estimator-bar-fill');
  const occupancyLabel = document.getElementById('estimator-occupancy-label');
  const discountBadge = document.getElementById('estimator-discount-badge');
  const topBadge = document.getElementById('estimator-top-badge');

  if (demandLabel) demandLabel.textContent = demandText;
  if (demandDot) {
    demandDot.className = `demand-dot demand-${demandLevel}`;
  }
  if (priceVal) priceVal.textContent = `R$ ${estPrice}`;
  if (barFill) {
    barFill.style.width = `${occupancyPct}%`;
    if (demandLevel === 'high') {
      barFill.style.background = 'linear-gradient(90deg, #F59E0B 0%, #F43F5E 100%)';
    } else if (demandLevel === 'medium') {
      barFill.style.background = 'linear-gradient(90deg, #14B8A6 0%, #0D9488 100%)';
    } else {
      barFill.style.background = 'linear-gradient(90deg, #10B981 0%, #059669 100%)';
    }
  }
  if (occupancyLabel) occupancyLabel.textContent = `${occupancyPct}% de cadeiras disponíveis`;
  if (discountBadge) discountBadge.textContent = `Até ${discountPct}% OFF`;
  if (topBadge) topBadge.textContent = `Economize até ${discountPct}%`;
}

function calculateFairPriceEngine(salon, service, windowType) {
  const basePrice = service.basePrice;
  // Piso operacional ético: 45% do preço base garante custos de insumos e comissão mínima
  const minOperatingCost = Math.max(30.00, Math.round(basePrice * 0.45));

  // Simula ocupação de acordo com o dia e janela de horário
  let occupancy = 0.42;
  if (windowType === 'now_2h') {
    occupancy = 0.28; // Maior ociosidade imediata = incentivo de última hora
  } else if (windowType === 'today_afternoon') {
    occupancy = 0.35; // Ociosidade de meio de tarde
  } else if (windowType === 'today_evening') {
    occupancy = 0.65; // Pico do fim de tarde
  } else if (windowType === 'tomorrow_morning') {
    occupancy = 0.30;
  }

  // Fator de Demanda e Oferta (Varia de 0.60 quando ocioso a 0.95 quando procurado)
  const demandSupplyFactor = 0.58 + (occupancy * 0.40);
  const rawFairPrice = basePrice * demandSupplyFactor;

  // Preço justo final respeitando o piso operacional
  const finalPrice = Math.max(minOperatingCost, Math.min(basePrice, Math.round(rawFairPrice)));
  const discountPct = Math.round(((basePrice - finalPrice) / basePrice) * 100);

  return {
    basePrice,
    finalPrice,
    discountPct,
    hasDiscount: discountPct > 0,
    occupancyRate: Math.round(occupancy * 100)
  };
}

let radarTimer1 = null;
let radarTimer2 = null;
let radarTimer3 = null;

function startInstantMatchRadar() {
  const windowKey = AppState.smartMatchWindow || 'now_2h';
  const serviceType = AppState.smartSelectedServiceType || 'hair_cut_fem';
  const maxRadiusKm = AppState.smartSelectedRadius || 3.0;

  trackEvent('instant_match_requested', {
    window: windowKey,
    service_type: serviceType,
    max_radius_km: maxRadiusKm
  });

  // Abre Modal
  const modal = document.getElementById('instant-match-overlay');
  const scanningState = document.getElementById('radar-scanning-state');
  const resultsState = document.getElementById('radar-results-state');
  const statusText = document.getElementById('radar-status-text');

  if (modal) modal.classList.add('active');
  if (scanningState) scanningState.style.display = 'flex';
  if (resultsState) resultsState.style.display = 'none';

  if (statusText) statusText.textContent = 'Mapeando cadeiras e profissionais disponíveis...';

  clearTimeout(radarTimer1);
  clearTimeout(radarTimer2);
  clearTimeout(radarTimer3);

  // Etapa 1 do Radar (0.6s)
  radarTimer1 = setTimeout(() => {
    if (statusText) statusText.textContent = 'Calculando custos operacionais e taxa de ociosidade...';
  }, 600);

  // Etapa 2 do Radar (1.2s)
  radarTimer2 = setTimeout(() => {
    if (statusText) statusText.textContent = 'Calibrando Tarifa Justa e ranqueando os 3 melhores matches...';
  }, 1200);

  // Etapa 3: Exibe Resultados (1.8s)
  radarTimer3 = setTimeout(() => {
    generateAndRenderMatches(windowKey, serviceType, maxRadiusKm);
  }, 1800);
}

function closeInstantMatchModal() {
  clearTimeout(radarTimer1);
  clearTimeout(radarTimer2);
  clearTimeout(radarTimer3);
  document.getElementById('instant-match-overlay')?.classList.remove('active');
}

function generateAndRenderMatches(windowKey, serviceType, maxRadiusKm) {
  const scanningState = document.getElementById('radar-scanning-state');
  const resultsState = document.getElementById('radar-results-state');
  if (scanningState) scanningState.style.display = 'none';
  if (resultsState) resultsState.style.display = 'block';

  // Filtra salões compatíveis
  const eligibleSalons = MOCK_SALONS.filter(s =>
    (s.category === 'hair' || s.category === 'barber' || s.category === 'mixed') &&
    s.distanceKm <= (maxRadiusKm + 0.5)
  );

  const pool = eligibleSalons.length >= 3 ? eligibleSalons : MOCK_SALONS.slice(0, 5);

  // Identifica serviço correspondente no salão ou usa padrão
  function getMatchingService(salon) {
    if (salon.services && salon.services.length > 0) {
      if (serviceType === 'hair_cut_masc') {
        const found = salon.services.find(s => s.category === 'barber' || s.name.toLowerCase().includes('barba') || s.name.toLowerCase().includes('masculino'));
        if (found) return found;
      } else if (serviceType === 'hair_treatment') {
        const found = salon.services.find(s => s.name.toLowerCase().includes('escova') || s.name.toLowerCase().includes('spa'));
        if (found) return found;
      }
      return salon.services[0];
    }
    return salon.service;
  }

  // Opção 1: Match Ideal (Melhor rating + melhor equilíbrio de desconto)
  const idealSalon = pool[0];
  const idealService = getMatchingService(idealSalon);
  const idealPricing = calculateFairPriceEngine(idealSalon, idealService, windowKey);

  // Opção 2: Mais Próximo (Menor distância)
  const sortedByDist = [...pool].sort((a, b) => a.distanceKm - b.distanceKm);
  const nearestSalon = sortedByDist[0].id !== idealSalon.id ? sortedByDist[0] : (sortedByDist[1] || pool[1]);
  const nearestService = getMatchingService(nearestSalon);
  const nearestPricing = calculateFairPriceEngine(nearestSalon, nearestService, windowKey);

  // Opção 3: Mais Econômico (Menor preço final justo)
  const sortedByPrice = [...pool].sort((a, b) => {
    const srvA = getMatchingService(a);
    const srvB = getMatchingService(b);
    const pA = calculateFairPriceEngine(a, srvA, windowKey).finalPrice;
    const pB = calculateFairPriceEngine(b, srvB, windowKey).finalPrice;
    return pA - pB;
  });
  let cheapestSalon = sortedByPrice.find(s => s.id !== idealSalon.id && s.id !== nearestSalon.id);
  if (!cheapestSalon) cheapestSalon = sortedByPrice[0];
  const cheapestService = getMatchingService(cheapestSalon);
  const cheapestPricing = calculateFairPriceEngine(cheapestSalon, cheapestService, windowKey);

  AppState.smartMatchOptions = [
    {
      type: 'ideal',
      tagClass: 'tag-ideal',
      tagText: '⭐ Melhor Custo-Benefício',
      salon: idealSalon,
      service: idealService,
      pricing: idealPricing,
      time: windowKey === 'now_2h' ? '14:30' : (windowKey === 'today_afternoon' ? '15:15' : '17:45'),
      etaWalk: '12 min a pé'
    },
    {
      type: 'nearest',
      tagClass: 'tag-nearest',
      tagText: `🚶 Mais Próximo (${nearestSalon.distanceKm} km)`,
      salon: nearestSalon,
      service: nearestService,
      pricing: nearestPricing,
      time: windowKey === 'now_2h' ? '14:45' : (windowKey === 'today_afternoon' ? '15:30' : '18:00'),
      etaWalk: '5 min a pé'
    },
    {
      type: 'cheapest',
      tagClass: 'tag-cheapest',
      tagText: `🏷️ Maior Economia (-${cheapestPricing.discountPct}% OFF)`,
      salon: cheapestSalon,
      service: cheapestService,
      pricing: cheapestPricing,
      time: windowKey === 'now_2h' ? '14:15' : (windowKey === 'today_afternoon' ? '14:45' : '17:30'),
      etaWalk: '15 min a pé'
    }
  ];

  AppState.selectedMatchOption = AppState.smartMatchOptions[0];

  trackEvent('instant_match_radar_completed', {
    options_found: AppState.smartMatchOptions.length,
    fair_price_selected: AppState.selectedMatchOption.pricing.finalPrice,
    merchant_id: AppState.selectedMatchOption.salon.id
  });

  renderMatchOptionsList();
}

function renderMatchOptionsList() {
  const container = document.getElementById('match-options-list');
  const confirmBtn = document.getElementById('btn-confirm-match-checkout');
  if (!container) return;

  container.innerHTML = '';

  AppState.smartMatchOptions.forEach((opt, idx) => {
    const isSelected = AppState.selectedMatchOption && AppState.selectedMatchOption.type === opt.type;
    const savingsAmount = Math.max(0, opt.pricing.basePrice - opt.pricing.finalPrice);

    const card = document.createElement('div');
    card.className = `match-ride-card ${isSelected ? 'selected' : ''}`;
    card.onclick = () => {
      AppState.selectedMatchOption = opt;
      renderMatchOptionsList();
      trackEvent('instant_match_option_selected', {
        match_type: opt.type,
        merchant_id: opt.salon.id,
        final_price: opt.pricing.finalPrice
      });
    };

    card.innerHTML = `
      <div class="match-ride-header">
        <div class="match-badge-tag ${opt.tagClass}">
          <span>${opt.tagText}</span>
        </div>
        <div class="match-selection-indicator" title="${isSelected ? 'Selecionado' : 'Clique para selecionar'}">
          <svg viewBox="0 0 24 24" fill="none" stroke-width="3"><polyline points="20 6 9 17 4 12"/></svg>
        </div>
      </div>
      <div class="match-ride-body">
        <div class="match-salon-info">
          <img class="match-thumb" src="${opt.salon.image}" alt="${opt.salon.name}" onerror="this.src='treatment_hair_salon.jpg'">
          <div>
            <div class="match-name">${opt.salon.name}</div>
            <div class="match-meta-line">
              <span>★ ${opt.salon.rating}</span> • <span>${opt.salon.neighborhood}</span> • <strong>${opt.time}</strong>
            </div>
            <div style="font-size:10px; color:var(--neutral-muted); margin-top:1px;">
              ${opt.service ? opt.service.name : 'Corte de Cabelo'} • ${opt.etaWalk}
            </div>
          </div>
        </div>
        <div class="match-price-column">
          <span class="match-base-slashed">R$ ${opt.pricing.basePrice.toFixed(2)}</span>
          <span class="match-fair-price">R$ ${opt.pricing.finalPrice.toFixed(2)}</span>
          <span class="match-savings-pill">Economize R$ ${savingsAmount.toFixed(2)}</span>
        </div>
      </div>
    `;

    container.appendChild(card);
  });

  // Atualiza botão CTA de confirmação com o valor do match selecionado
  if (confirmBtn && AppState.selectedMatchOption) {
    const label = confirmBtn.querySelector('span');
    if (label) {
      label.textContent = `Confirmar Reserva com Tarifa Justa (R$ ${AppState.selectedMatchOption.pricing.finalPrice.toFixed(2)})`;
    }
  }
}

function confirmMatchAndProceed() {
  const match = AppState.selectedMatchOption;
  if (!match) return;

  AppState.selectedSalon = match.salon;
  AppState.selectedService = match.service || match.salon.service;
  AppState.selectedStaff = match.salon.staff[0];
  AppState.selectedTime = match.time;
  AppState.currentPricing = {
    basePrice: match.pricing.basePrice,
    finalPrice: match.pricing.finalPrice,
    discountPct: match.pricing.discountPct,
    hasDiscount: match.pricing.hasDiscount,
    type: 'economy',
    badgeText: 'Tarifa Justa Dinâmica',
    subtext: `calibrado por demanda e ociosidade local (${match.pricing.occupancyRate}% ocupação)`
  };

  closeInstantMatchModal();
  showToast(`Match confirmado com ${match.salon.name}! Abrindo checkout...`);
  navigateTo('checkout');
}

// ===================================================================
// 9. TELA DEDICADA: MEUS AGENDAMENTOS (HISTÓRICO E GESTÃO)
// ===================================================================
function switchAppointmentTab(tabKey) {
  AppState.appointmentsTab = tabKey;
  document.getElementById('tab-appt-active')?.classList.toggle('active', tabKey === 'active');
  document.getElementById('tab-appt-history')?.classList.toggle('active', tabKey === 'history');
  renderAppointmentsScreen();
}

function renderAppointmentsScreen() {
  const container = document.getElementById('appointments-list-container');
  if (!container) return;

  container.innerHTML = '';
  const isHistory = AppState.appointmentsTab === 'history';

  // Filtra por status
  const filtered = AppState.appointmentsList.filter(appt => {
    if (isHistory) {
      return appt.status === 'COMPLETED' || appt.status === 'CANCELLED_BY_USER' || appt.status === 'CANCELLED_BY_MERCHANT';
    } else {
      return appt.status === 'CONFIRMED' || appt.status === 'IN_PROGRESS';
    }
  });

  const activeCount = AppState.appointmentsList.filter(a => a.status === 'CONFIRMED' || a.status === 'IN_PROGRESS').length;
  const countBadge = document.getElementById('appt-count-badge');
  if (countBadge) {
    countBadge.textContent = `${activeCount} ${activeCount === 1 ? 'Ativo' : 'Ativos'}`;
  }

  if (filtered.length === 0) {
    container.innerHTML = `
      <div style="text-align:center; padding:32px 14px; color:var(--neutral-muted);">
        <svg width="36" height="36" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" style="margin-bottom:8px; opacity:0.6;"><rect x="3" y="4" width="18" height="18" rx="2" ry="2"/><line x1="16" y1="2" x2="16" y2="6"/><line x1="8" y1="2" x2="8" y2="6"/><line x1="3" y1="10" x2="21" y2="10"/></svg>
        <p style="font-size:13px; font-weight:700;">Nenhum agendamento ${isHistory ? 'no histórico' : 'em aberto'}</p>
        <p style="font-size:11px; margin-top:4px;">${isHistory ? 'Seus serviços finalizados ou cancelados aparecerão aqui.' : 'Que tal aproveitar nosso corte inteligente por tarifa justa?'}</p>
        ${!isHistory ? `<button class="card-action-btn" style="margin-top:14px;" onclick="navigateTo('home')">Agendar Agora</button>` : ''}
      </div>
    `;
    return;
  }

  filtered.forEach(appt => {
    const card = document.createElement('div');
    card.className = 'appt-card';

    let statusClass = 'status-confirmed';
    let statusLabel = 'Confirmado';

    if (appt.status === 'COMPLETED') {
      statusClass = 'status-completed';
      statusLabel = 'Concluído';
    } else if (appt.status === 'CANCELLED_BY_USER') {
      statusClass = 'status-cancelled-user';
      statusLabel = 'Cancelado por Você';
    } else if (appt.status === 'CANCELLED_BY_MERCHANT') {
      statusClass = 'status-cancelled-merchant';
      statusLabel = 'Cancelado pelo Salão (Força Maior)';
    }

    card.innerHTML = `
      <div class="appt-card-top">
        <span class="appt-status-badge ${statusClass}">${statusLabel}</span>
        <span style="font-size:11px; font-weight:800; color:var(--primary);">${appt.id}</span>
      </div>

      <div class="appt-card-body">
        <img class="appt-thumb" src="${appt.salon.image || 'treatment_hair_salon.jpg'}" alt="${appt.salon.name}" onerror="this.src='treatment_hair_salon.jpg'">
        <div style="flex:1;">
          <h4 class="appt-info-name">${appt.salon.name}</h4>
          <div class="appt-info-service">${appt.service.name}</div>
          <div class="appt-info-datetime">
            <span>📅 ${appt.date || 'Hoje'} às ${appt.time}</span> • <span>${appt.staff ? appt.staff.name : 'Equipe'}</span>
          </div>
          <div style="font-size:12px; font-weight:800; color:var(--primary); margin-top:4px;">
            R$ ${(appt.pricing?.finalPrice || appt.service.basePrice).toFixed(2)}
            ${appt.pricing?.hasDiscount ? `<span style="font-size:10px; color:var(--promo-coral); font-weight:700;">(-${appt.pricing.discountPct}%)</span>` : ''}
          </div>
          ${appt.cancellationReason ? `<div style="font-size:10px; color:#C2410C; margin-top:4px; font-style:italic;">Motivo: ${appt.cancellationReason}</div>` : ''}
        </div>
      </div>

      <div class="appt-card-actions">
        ${appt.status === 'CONFIRMED' ? `
          <button class="appt-btn-outline" onclick="openVoucherForAppt('${appt.id}')">Ver Voucher</button>
          <button class="appt-btn-outline" style="color:var(--error); border-color:#FECACA;" onclick="openCancelModalForAppt('${appt.id}')">Cancelar</button>
        ` : `
          <button class="appt-btn-primary" onclick="rebookSalon('${appt.salon.id}')">Agendar Novamente</button>
        `}
      </div>
    `;

    container.appendChild(card);
  });
}

function openVoucherForAppt(apptId) {
  const appt = AppState.appointmentsList.find(a => a.id === apptId);
  if (appt) {
    AppState.confirmedAppointment = appt;
    navigateTo('confirm');
  }
}

let cancelingApptId = null;
function openCancelModalForAppt(apptId) {
  cancelingApptId = apptId;
  openCancelModal();
}

function rebookSalon(salonId) {
  const salon = MOCK_SALONS.find(s => s.id === salonId);
  if (salon) {
    openSalonDetail(salon.id);
  } else {
    navigateTo('home');
  }
}

// ===================================================================
// 10. MÁQUINA DE ESTADOS FORMAL: appointment_status_changed (Seção 8 e 11)
// ===================================================================
function emitAppointmentStatusChanged(appointmentId, fromStatus, toStatus, extraProps = {}) {
  trackEvent('appointment_status_changed', {
    appointment_id: appointmentId,
    previous_status: fromStatus,
    new_status: toStatus,
    ...extraProps
  });
}

// ===================================================================
// MOTOR DE CÁLCULO DE VALIDAÇÃO DE HIPÓTESES (H1, H2, H4) — Seção 3 e Seção 11
// ===================================================================
function calculateH1Report() {
  const events = JSON.parse(localStorage.getItem('bp_analytics_events') || '[]');
  const slotViews = events.filter(e => e.eventName === 'slot_viewed');
  const slotSelects = events.filter(e => e.eventName === 'slot_selected');
  const checkouts = events.filter(e => e.eventName === 'checkout_completed');

  const discountViews = slotViews.filter(e => e.props && e.props.has_discount);
  const regularViews = slotViews.filter(e => e.props && !e.props.has_discount);

  const discountBookings = checkouts.filter(e => e.props && e.props.discount_applied > 0);
  const regularBookings = checkouts.filter(e => e.props && (!e.props.discount_applied || e.props.discount_applied === 0));

  const totalBookings = checkouts.length;
  const discountBookingRatio = totalBookings > 0 ? (discountBookings.length / totalBookings) * 100 : 0;
  
  // Taxa de conversão por tipo de slot (slot_selected / slot_viewed)
  const discountConversion = discountViews.length > 0 
    ? ((slotSelects.filter(s => s.props && s.props.has_discount).length / discountViews.length) * 100) 
    : 0;

  return {
    discountViewsCount: discountViews.length,
    regularViewsCount: regularViews.length,
    discountBookingsCount: discountBookings.length,
    regularBookingsCount: regularBookings.length,
    totalBookings,
    discountBookingRatio: discountBookingRatio.toFixed(1),
    discountConversion: discountConversion.toFixed(1),
    // H1 validada se >60% dos agendamentos optaram por horários com tarifa dinâmica
    isValidated: totalBookings >= 3 && discountBookingRatio >= 60
  };
}

function calculateH2Report() {
  const events = JSON.parse(localStorage.getItem('bp_analytics_events') || '[]');
  const checkoutsStarted = events.filter(e => e.eventName === 'checkout_started');
  const checkoutsCompleted = events.filter(e => e.eventName === 'checkout_completed');
  const cardsValidated = events.filter(e => e.eventName === 'card_validated');
  const abandonments = events.filter(e => e.eventName === 'checkout_abandoned');

  const totalStarted = checkoutsStarted.length;
  const totalCompleted = checkoutsCompleted.length;
  const completionRate = totalStarted > 0 ? (totalCompleted / totalStarted) * 100 : 0;

  return {
    checkoutsStarted: totalStarted,
    checkoutsCompleted: totalCompleted,
    cardsValidated: cardsValidated.length,
    abandonments: abandonments.length,
    completionRate: completionRate.toFixed(1),
    // H2 validada se taxa de conversão do checkout com pré-autorização for >= 40%
    isValidated: totalStarted >= 2 && completionRate >= 40
  };
}

function renderValidationMetrics() {
  const container = document.getElementById('validation-dashboard-metrics');
  if (!container) return;

  const h1 = calculateH1Report();
  const h2 = calculateH2Report();

  container.innerHTML = `
    <div class="metrics-grid">
      <div class="metric-card-kpi ${h1.isValidated ? 'validated' : ''}">
        <div class="metric-kpi-header">
          <span class="metric-kpi-title">H1: Tarifa Dinâmica</span>
          <span class="metric-kpi-badge ${h1.isValidated ? 'badge-validated' : 'badge-tracking'}">
            ${h1.isValidated ? 'Validada' : 'Em Coleta'}
          </span>
        </div>
        <div class="metric-kpi-number">${h1.discountBookingRatio}%</div>
        <div class="metric-kpi-desc">Agendamentos com desconto (Meta: &ge; 60%)</div>
        <div class="metric-kpi-sub">
          <span>${h1.discountBookingsCount} com desc. / ${h1.totalBookings} total</span>
          <span>Conv: ${h1.discountConversion}%</span>
        </div>
      </div>

      <div class="metric-card-kpi ${h2.isValidated ? 'validated' : ''}">
        <div class="metric-kpi-header">
          <span class="metric-kpi-title">H2: Pré-Autorização</span>
          <span class="metric-kpi-badge ${h2.isValidated ? 'badge-validated' : 'badge-tracking'}">
            ${h2.isValidated ? 'Validada' : 'Em Coleta'}
          </span>
        </div>
        <div class="metric-kpi-number">${h2.completionRate}%</div>
        <div class="metric-kpi-desc">Conclusão de Checkout (Meta: &ge; 40%)</div>
        <div class="metric-kpi-sub">
          <span>${h2.checkoutsCompleted} pagos / ${h2.checkoutsStarted} iniciados</span>
          <span>Aband: ${h2.abandonments}</span>
        </div>
      </div>
    </div>
  `;
}

function resetValidationData() {
  if (confirm('Deseja realmente limpar todos os eventos de teste e validação acumulados? Esta ação não remove sua conta de participante.')) {
    localStorage.removeItem('bp_analytics_events');
    AppState.analyticsEvents = [];
    showToast('Eventos de validação reiniciados com sucesso!');
    renderValidationMetrics();
  }
}

function resetPrototypeState() {
  navigateTo('home');
  showToast('Protótipo reiniciado para a tela inicial.');
}

// Alternador de Visualizador Mobile / Tela Cheia
function toggleFullscreenMode() {
  document.body.classList.toggle('fullscreen-mode');
  const isFull = document.body.classList.contains('fullscreen-mode');
  const label = document.getElementById('fullscreen-btn-label');
  if (label) {
    label.textContent = isFull ? 'Ver Moldura' : 'Tela Cheia';
  }
}

// ===================================================================
// 11. INICIALIZAÇÃO
// ===================================================================
function initMapBottomSheetDrag() {
  const sheet = document.querySelector('.map-bottom-sheet');
  const handle = document.querySelector('.sheet-handle');
  if (!sheet || !handle) return;

  let startY = 0;
  let currentY = 0;
  let isDragging = false;
  let isExpanded = false;

  const onTouchStart = (e) => {
    startY = e.touches ? e.touches[0].clientY : e.clientY;
    isDragging = true;
    sheet.style.transition = 'none';
  };

  const onTouchMove = (e) => {
    if (!isDragging) return;
    currentY = e.touches ? e.touches[0].clientY : e.clientY;
    const deltaY = currentY - startY;

    if (!isExpanded && deltaY < 0) {
      // Puxando para cima para expandir
      const translateY = Math.max(-180, deltaY);
      sheet.style.transform = `translateY(${translateY}px)`;
    } else if (isExpanded && deltaY > 0) {
      // Puxando para baixo para colapsar
      const translateY = -180 + Math.min(180, deltaY);
      sheet.style.transform = `translateY(${translateY}px)`;
    }
  };

  const onTouchEnd = () => {
    if (!isDragging) return;
    isDragging = false;
    sheet.style.transition = 'transform 0.3s cubic-bezier(0.16, 1, 0.3, 1)';
    const deltaY = currentY - startY;

    if (!isExpanded && deltaY < -40) {
      isExpanded = true;
      sheet.style.transform = 'translateY(-180px)';
    } else if (isExpanded && deltaY > 40) {
      isExpanded = false;
      sheet.style.transform = 'translateY(0)';
    } else {
      sheet.style.transform = isExpanded ? 'translateY(-180px)' : 'translateY(0)';
    }
  };

  handle.addEventListener('mousedown', onTouchStart);
  window.addEventListener('mousemove', onTouchMove);
  window.addEventListener('mouseup', onTouchEnd);

  handle.addEventListener('touchstart', onTouchStart, { passive: true });
  window.addEventListener('touchmove', onTouchMove, { passive: true });
  window.addEventListener('touchend', onTouchEnd);
}

document.addEventListener('DOMContentLoaded', () => {
  // Configura slider
  const slider = document.getElementById('clock-time-slider');
  if (slider) {
    slider.max = TIME_SLOTS.length - 1;
    slider.value = AppState.selectedTimeSlotIndex;
    slider.addEventListener('input', (e) => onSliderTimeChange(e.target.value));
  }

  // Inicializa arrasto do Bottom Sheet no Mapa
  initMapBottomSheetDrag();

  // Inicializa Sessão ou Onboarding
  initSessionState();
});


