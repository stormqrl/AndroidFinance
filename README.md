# AndroidFinance 💰

Um aplicativo Android para controle financeiro pessoal que permite gerenciar receitas e despesas de forma simples e intuitiva.

## 📋 Descrição

O AndroidFinance é um aplicativo de controle financeiro desenvolvido em Kotlin que atende aos requisitos de um sistema completo de gestão de transações financeiras. O app permite aos usuários registrar, visualizar, editar e excluir suas receitas e despesas, além de oferecer funcionalidades de pesquisa e favoritos para melhor organização.

## ⚡ Funcionalidades

1. **Cadastro de Transações** - Permite adicionar novas receitas e despesas com descrição, valor, data e categoria.

2. **Listagem de Registros** - Exibe todas as transações cadastradas em uma lista organizada e navegável.

3. **Pesquisa e Filtros** - Oferece busca por texto e filtros por tipo (receita/despesa) e data para facilitar a localização de registros.

4. **Edição e Exclusão** - Permite modificar ou remover transações existentes com confirmação de segurança.

5. **Sistema de Favoritos** - Possibilita marcar transações importantes como favoritas para acesso rápido.

## 🛠️ Tecnologias e Bibliotecas

- **Linguagem**: Kotlin
- **Arquitetura**: MVVM (Model-View-ViewModel)
- **UI**: 
  - AndroidX AppCompat
  - Material Design Components
  - View Binding
  - Navigation Component
- **Banco de Dados**: 
  - Room Database
  - Room KTX
- **Programação Assíncrona**: 
  - Kotlin Coroutines
  - LiveData
- **Injeção de Dependência**: Factory Pattern
- **RecyclerView**: Para listagem eficiente
- **Fragment KTX**: Para navegação entre telas

## 🚀 Como Executar

### Pré-requisitos
- **Android Studio**: Versão mais recente
- **SDK Android**: Mínimo API 24 (Android 7.0)
- **Kotlin**: Versão compatível com o projeto
- **Java**: JDK 11

### Passos para Instalação

1. **Clone o repositório**:
   ```bash
   git clone [URL_DO_REPOSITORIO]
   cd AndroidFinance
   ```

2. **Abra no Android Studio**:
   - Abra o Android Studio
   - Selecione "Open an existing project"
   - Navegue até a pasta do projeto e selecione

3. **Sincronize as dependências**:
   - O Android Studio automaticamente baixará as dependências
   - Aguarde a sincronização do Gradle finalizar

4. **Configure um dispositivo**:
   - Use um dispositivo físico com Android 7.0+ ou
   - Configure um emulador Android no AVD Manager

5. **Execute o aplicativo**:
   - Clique no botão "Run" (▶️) ou pressione Shift+F10
   - Selecione o dispositivo target

## 📱 Requisitos do Sistema

- **API Mínima**: Android 7.0 (API 24)
- **API Target**: Android 14 (API 35)
- **Permissões**: Nenhuma permissão especial necessária

## 🧪 Executando Testes

### Testes Unitários
```bash
./gradlew test
```

### Testes Instrumentados
```bash
./gradlew connectedAndroidTest
```

## 📂 Estrutura do Projeto

```
app/src/main/java/com/example/androidfinance/
├── data/                   # Camada de dados
│   ├── converter/         # Conversores do Room
│   ├── dao/              # Data Access Objects
│   ├── database/         # Configuração do banco
│   ├── entity/           # Entidades do banco
│   ├── mapper/           # Mapeadores de dados
│   └── repository/       # Repositórios
├── domain/               # Camada de domínio
│   └── model/           # Modelos de negócio
└── presentation/        # Camada de apresentação
    ├── adapter/        # Adaptadores do RecyclerView
    ├── fragment/       # Fragments da UI
    └── viewmodel/      # ViewModels
```
