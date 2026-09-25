# Validação executada

Data da execução: 24/09/2026, 20:47:50 (America/Sao_Paulo).

## Resultados

| Verificação | Resultado |
|---|---|
| Compilação e empacotamento em JAR | Concluídos com Maven 3.9.2 e JDK 17 |
| Suíte automatizada com H2 em modo PostgreSQL | 27 testes, sem falhas |
| Suíte automatizada com PostgreSQL 16.4 real | 27 testes, sem falhas, erros ou testes ignorados |
| Flyway em PostgreSQL | V1 a V5 aplicadas e validadas; mapeamento Hibernate validado |
| Collection: CRUD e ViaCEP real | 30 requisições, 58 verificações, sem falhas |
| Collection: resiliência com simulador | 6 requisições, 12 verificações, sem falhas |

As chamadas HTTP da collection foram executadas pelo Newman, executor de collections do Postman. As chamadas de disponibilidade da primeira pasta acessaram a ViaCEP real. Os cenários de falha da segunda pasta usaram o simulador local incluído no projeto, com timeout real de leitura, erro HTTP, conexão interrompida e respostas inválidas. A última chamada confirmou que a API continuava funcionando após as falhas.

O ambiente de validação usou PostgreSQL temporário na porta 55432. O projeto entregue mantém a porta padrão 5432. As credenciais do ambiente temporário e as ferramentas portáteis não fazem parte da entrega. O JAR foi executado em duas instâncias para os testes HTTP.

## Cobertura funcional

- Criação, listagem, atualização de todos os campos e inativação sem exclusão física.
- Busca por ID, filtros por categoria, top 5 mais caros e exclusão de inativos dos resultados.
- Path Variable, Request Param, Request Header e Request Body.
- Campos obrigatórios, preço positivo, cidade permitida e JSON inválido.
- HTTP 404 coerente para produtos inexistentes/inativos.
- Disponibilidade verdadeira nas três cidades e falsa para cidade diferente.
- CEP com hífen, formato inválido, parâmetro ausente e CEP inexistente.
- Respostas HTTP 502, 503 e 504 conforme o tipo de falha externa.
- Preenchimento dos produtos da migration com as três cidades.

## Evidências

- [Resultados Java: contexto Spring](evidencias/com.example.crud.CrudApplicationTests.txt)
- [Resultados Java: API](evidencias/com.example.crud.ProductApiTests.txt)
- [Resumo das requisições e verificações reais](evidencias/newman-main.json)
- [Resumo das requisições e verificações de falha](evidencias/newman-fault.json)

Os arquivos JSON são resumos extraídos dos relatórios reais do Newman, com horários, contagens, nomes dos testes, códigos HTTP e resultado de cada verificação. Não são relatórios exportados pela interface gráfica do Postman.

## Etapas ainda a realizar pelo aluno

- Importar e executar a collection na interface do Postman e exportar a versão utilizada.
- Criar o repositório remoto, publicar o código e a collection e enviar o link.
- Conferir o resultado do GitHub Actions depois da publicação.
- Preencher o prazo definido pelo professor.

Essas etapas não foram declaradas como concluídas. O passo a passo está em [COMO_ENTREGAR.txt](../COMO_ENTREGAR.txt).
