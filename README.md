# 🚦 Smart Traffic Control - Simulador de Tráfego Urbano
Bem-vindo ao **Smart Traffic Control**, um simulador de tráfego urbano desenvolvido em Java. Este projeto permite simular o comportamento de veículos em uma malha urbana utilizando mapas reais, controle inteligente de semáforos e visualização gráfica do fluxo de tráfego.

---

## ✨ Funcionalidades Principais

- 📍 **Importação de mapas reais** no formato JSON (ruas, interseções e semáforos)
- 🚗 Simulação dinâmica de veículos com **rotas automáticas**
- 🚦 **Controle inteligente de semáforos** com 3 modelos operacionais:
  - Ciclo fixo
  - Otimização baseada no tamanho da fila
  - Otimização baseada em consumo e horários de pico
- 🖥️ Interface gráfica interativa com visualização em tempo real
- 📊 Geração de estatísticas:
  - Tempo médio de viagem
  - Tempo médio parado
  - Viagens concluídas
- 💾 Sistema de save/load para continuar simulações

## 🗂️ Estrutura do Projeto

```bash
smart-traffic-control/
├── src/
│   ├── controller/    # Lógica de controle da simulação
│   ├── datastructure/ # Estruturas de dados customizadas
│   ├── gui/           # Interface gráfica (Swing)
│   ├── model/         # Entidades do sistema
│   ├── service/       # Serviços de carregamento
│   └── Main.java      # Ponto de entrada
├── saves/             # Simulações salvas
├── mapas/             # Mapas JSON
└── README.md
```

---

## 🚀 Como Executar

### 1. Pré-requisitos

* Java 17 ou superior
* Maven

### 2. Clone o repositório

```bash
git clone https://github.com/seu-usuario/smart-traffic-control.git
cd smart-traffic-control
```

### 3. Compile e execute:

```bash
javac -d bin src/**/*.java
cd bin
java org.icev.smarttrafficcontrol.Main
```
---
### 4. A interface gráfica será aberta.

## 🧭 Como Usar

* Selecione um mapa JSON.
* Configure:
  - Número de veículos por ciclo
  - Modelo de controle dos semáforos
* Controle a simulação pelos botões:
  - ▶️ Iniciar
  - ⏸️ Pausar
  - ⏹️ Parar
  - 💾 Salvar
  - 📂 Carregar
* Acompanhe na tela o movimento dos veículos, o estado dos semáforos e as estatísticas em tempo real.

---

## 🛠️ Tecnologias Utilizadas

* Java 17+
* Maven
* Swing (interface gráfica)
* Jackson (leitura de JSON)
* Spring Boot

---

## 👨‍💻 Autores

Desenvolvido por **\[ Luiz Henrique e Renan Sampaio ]** para a disciplina de **Estrutura de Dados**, como projeto de simulação urbana inteligente.
