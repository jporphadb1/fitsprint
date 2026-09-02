# Ideia: Triagem automática de boletas com falha de envio (Falabella/Forus)

**Problema:** quando o envio de uma boleta fiscal falha (Falabella/Forus Chile), o operador de suporte precisa interpretar manualmente o erro retornado pelo marketplace para decidir a ação — reenviar, dividir o pacote, ou corrigir um dado faltante.

**Ideia de IA Embarcada:** um agente embarcado no fluxo de sincronização que lê o response de erro do marketplace no momento da falha e sugere automaticamente, na mesma tela de suporte, qual das três ações aplicar — reduzindo o tempo de triagem manual e a dependência de conhecimento tribal sobre os códigos de erro.

**Base real:** discovery já realizado no PBI #1095186 (envío de boletas múltiples), incluindo reunião com o time (Nico, Pablo) e mapeamento dos principais cenários de erro do fluxo atual.

**Próximos passos:** validar viabilidade técnica com o time (fonte dos dados de erro, onde plugar a sugestão na UI de suporte) antes de avançar para uma spec formal.