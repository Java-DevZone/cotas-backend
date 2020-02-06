# Sistema de Cotas
Calcular o lucro dos investimentos de cada participante do fundo/clube

## Rodar o Docker do Mysql

* Primeiro, devemos criar o volume lógico do docker para manter os dados do sistema;

```
docker volume create cotas-mysql
```

* Em seguida, devemos executar o container, utilizando o volume criado como referência;
* O comando abaixo, executa o container docker na porta **3306** e com a senha de *root* = **123456**;

```
docker run -d --rm --name mysql-server -v cotas-mysql:/var/lib/mysql -p 3306:3306 -e MYSQL_ROOT_PASSWORD=123456 -e MYSQL_DATABASE=cotas mysql:8.0
```

## API Calls Examples

[Postman Documentation](https://documenter.getpostman.com/view/984544/SWTG6bCs)

