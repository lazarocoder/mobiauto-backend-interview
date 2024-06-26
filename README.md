# Tradução do READ.ME

[![Traduzir para Inglês](https://img.shields.io/badge/traduzir-Google%20Translate-blue)](https://translate.google.com/translate?sl=auto&tl=en&u=https://github.com/lazarocoder/mobiauto-backend-interview)

# Introdução

O projeto de avaliação da “Mobiauto” consiste em um sistema de gestão de revendas de veículos, projetado para tornar os processos de revendas mais eficiente e eficaz. O sistema inclui funcionalidades de cadastro de usuários, revendedoras e oportunidades de revenda, que são gerenciadas pelos funcionários da loja em questão.

As oportunidades de revenda são distribuídas automaticamente aos funcionários que estão há mais tempo sem receber uma tarefa. Quando uma oportunidade é concluída, o funcionário deve fornecer o motivo da conclusão, e o horário de finalização é registrado.

A aplicação utiliza "Basic Authentication", requerendo que o usuário informe seu e-mail e senha antes de realizar qualquer requisição. O sistema usa o banco de dados PostgreSQL (versão 14.10), que é criado e gerenciado via Docker, com integração ao pgAdmin4.

## Pré-requisitos

- Java JDK 17.0.10
- Maven
- IntelliJ IDEA
- Docker
- pgAdmin4 – Para realização de consultas de dados

## Como iniciar o projeto

1. Abra a IDE de sua preferência e carregue o projeto “mobiauto-backend-interview-api”.
2. Aguarde até que todas as dependências sejam resolvidas e baixadas automaticamente.
3. Após a conclusão do download das dependências, execute as tarefas “maven clean” e, em seguida, “maven install” para garantir uma compilação limpa e a instalação dos artefatos.
   - Em caso de erros durante o download dos artefatos, execute: `mvn dependency:purge-local-repository install`
4. Navegue até a pasta profiles, para iniciar o container, execute no seu terminal: `docker-compose up`. Para executar em segundo plano, adicione a flag `-d`: `docker-compose up -d`.
5. Navegue até a classe principal da aplicação, `MobiautoBackendInterviewApiApplication.java`, e inicie a aplicação.
6. Abra seu navegador e acesse: http://localhost:8080/swagger-ui/index.html para visualizar a documentação interativa (Swagger) da API.

## Dados de acesso do authentication no swagger

- E-mail do administrador: Administrador@email.com
- Senha do administrador: 8080

## Principais dependências utilizadas

- Spring Boot: Versão 3.3.1
- PostgreSQL
- Lombok
- Spring Boot Starter Test
- Spring Boot Starter Security
- Spring Boot Starter data JPA
- Springdoc Openapi Starter Webmvc Ui

## Da arquitetura

- **Config**
  - Foco: Armazenar classes de configuração.
  - Detalhes: Contém classes que configuram diversos aspectos da aplicação, como beans do Spring, configuração de segurança, propriedades de conexão com banco de dados, entre outros.
- **Controller**
  - Foco: Controladores da aplicação.
  - Detalhes: Contém classes que lidam com as requisições HTTP (GET, POST, PUT, DELETE), responsáveis por receber, processar e responder às requisições.
- **Dto**
  - Foco: Objetos de Transferência de Dados (DTO).
  - Detalhes: Utilizados para transferir dados entre diferentes camadas da aplicação, sem expor as entidades de domínio diretamente.
- **Enumerated**
  - Foco: Tipos Enumerados.
  - Detalhes: Contém enums que representam um conjunto fixo de constantes, como tipos de cargo, estados de um pedido, etc.
- **Exception**
  - Foco: Tratamento de Exceções.
  - Detalhes: Armazena classes que lidam com a captura e tratamento de exceções específicas da aplicação.
- **Job**
  - Foco: Tarefas Automatizadas.
  - Detalhes: Contém classes que representam tarefas executadas automaticamente, como jobs agendados ou processos de inicialização.
- **Model**
  - Foco: Modelos de Dados.
  - Detalhes: Representam as entidades do domínio da aplicação, mapeadas para tabelas no banco de dados.

   ![Mobiauto-UML](https://github.com/lazarocoder/mobiauto-backend-interview/assets/63754729/b928cdaa-eeec-4fc8-8aa8-daa9ffe9059b)

- **Security**
  - Foco: Configurações e Implementações de Segurança.
  - Detalhes: Gerencia aspectos de segurança da aplicação, como autenticação e autorização.
- **Service**
  - Foco: Lógica de Negócio.
  - Detalhes: Contém classes que realizam operações de banco de dados e processam requisições.
- **Util**
  - Foco: Utilitários.
  - Detalhes: Funções auxiliares utilizadas em várias partes da aplicação, incluindo manipulação de strings, datas, validações comuns, etc.

## Dos testes unitários

Segue os resultados da cobertura de testes. Para executar os testes, basta acessar a pasta raiz do projeto e clicar na opção "Run with coverage".

![Cobertura de testes](https://github.com/lazarocoder/mobiauto-backend-interview/assets/63754729/29c75667-a285-49c4-9fdc-521b573585f6)


## Tecnico responsável

- **Nome:** Lázaro Daniel
- **E-mail:** Lazarodaniel80@gmail.com
- **LinkedIn:** [Perfil LinkedIn](https://www.linkedin.com/in/l%C3%A1zaro-silva-desenvolvedor/?locale=pt_BR)
