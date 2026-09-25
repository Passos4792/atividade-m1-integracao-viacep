# Atividade M1 2509

Java Spring Fundamentals — Integração de Sistemas — Aula 4

## Descrição da atividade

Integrar uma API de produtos ao serviço ViaCEP para verificar a disponibilidade de um produto em uma cidade. O endpoint recebe o ID do produto e o CEP, consulta a cidade correspondente na ViaCEP e compara com o centro de distribuição do produto. Deve retornar `true` quando as cidades forem iguais e `false` quando forem diferentes.

A atividade é cumulativa e mantém as funcionalidades desenvolvidas nas Aulas 1 a 3.

## Requisitos das Aulas 1 a 3

- Spring Boot com PostgreSQL e migrations Flyway.
- CRUD de produtos: criar, listar, atualizar e inativar.
- Validação dos dados de entrada e tratamento de exceções.
- Uso de Path Variable, Request Param, Request Header e Request Body.
- Consultas por categoria, por ID e top 5 produtos por preço.

## Requisitos da Aula 4

- Adicionar o campo `distribution_center` na tabela `product` por migration Flyway.
- Distribuir os produtos existentes entre Mogi das Cruzes, Recife e Porto Alegre.
- Mapear o campo na entidade de produto.
- Criar um serviço com `@Service` para consultar `https://viacep.com.br/ws/{cep}/json/`.
- Criar um endpoint que receba ID do produto e CEP e chame o serviço.
- Comparar a cidade retornada pela ViaCEP com o centro de distribuição.
- Tratar CEP mal formatado, CEP inexistente e indisponibilidade da ViaCEP.
- Testar o endpoint pelo Postman e incluir a collection exportada no repositório.

## Endpoint implementado

`GET /product/{id}/availability?cep=08773380`

Respostas:

- `200`: booleano `true` ou `false`.
- `400`: CEP inválido ou ausente.
- `404`: produto inexistente/inativo ou CEP inexistente.
- `502`: resposta inválida da ViaCEP.
- `503`: indisponibilidade ou falha de conexão da ViaCEP.
- `504`: tempo limite na consulta à ViaCEP.

## Critérios de entrega

- Descrição da atividade na pasta `/docs`.
- Projeto completo na branch `feature` do Git.
- Link do repositório com o código-fonte.
- Collection exportada em `postman/Atividade-M1.postman_collection.json`.
- Endpoint testado no Postman e funcionalidades das aulas anteriores preservadas.

A pasta de CRUD e ViaCEP real foi executada no Postman com 58 verificações aprovadas, nenhuma falha e nenhum erro.
