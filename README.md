<h1 align="center">
    <img alt="Java DevZone" src="https://images-ext-1.discordapp.net/external/aPHCjuIeNxdUR5iRYu63gLvZYwx7acahDQrLNJt8JZc/https/static-cdn.jtvnw.net/jtv_user_pictures/8788e662-173a-4de6-b4fd-5cc534361c4d-profile_image-300x300.png" width="200px" />
</h1>

<h2 align="center">
    Backend for Quotas System
</h2>

<!--- Melhore ou adicione mais pelo link https://shields.io --->
<p align="center">
<img alt="GitHub " src="https://img.shields.io/github/license/Java-DevZone/cotas-backend?color=d42a2a&style=for-the-badge">

<img alt="GitHub contributors count" src="https://img.shields.io/github/contributors/Java-DevZone/cotas-backend?style=for-the-badge&color=d42a2a">

<img alt="GitHub stars count" src="https://img.shields.io/github/stars/Java-DevZone/cotas-backend?style=for-the-badge&color=d42a2a">

<img alt="GitHub forks count" src="https://img.shields.io/github/forks/Java-DevZone/cotas-backend?style=for-the-badge&color=d42a2a">

<img alt="Travis (.org)" src="https://img.shields.io/travis/Java-DevZone/cotas-backend?style=for-the-badge&color=d42a2a">

</p>

# Descrição
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

XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX