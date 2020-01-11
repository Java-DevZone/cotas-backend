# Sistema de Cotas
Calcular o lucro dos investimentos de cada participante do fundo/clube

## Rodar o Docker do Mysql
```
docker run -d --rm --name mysql-server -p 3306:3306 -e MYSQL_ROOT_PASSWORD=123456 -e MYSQL_DATABASE=cotas mysql:8.0
```

## Criar/Atualizar a base de dados
```
mvn flyway:migrate
```

Novo código comitado!
