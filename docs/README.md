# Atividade M1 2509

A atividade consiste em consultar a ViaCEP usando o CEP e o ID de um produto para verificar se a cidade é a mesma do seu centro de distribuição.

Endpoint: `GET /product/{id}/availability?cep=08773380`

Retorna `true` se as cidades forem iguais e `false` se forem diferentes. Os centros de distribuição são Mogi das Cruzes, Recife e Porto Alegre. CEP inválido, produto não encontrado e falhas na ViaCEP são tratados pela API.

O projeto mantém o CRUD, as validações e as consultas das aulas anteriores, usando Spring Boot, PostgreSQL e Flyway. A collection de testes está na pasta `postman`, e a entrega está na branch `feature`.
