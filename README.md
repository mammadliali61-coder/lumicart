# LumiCart

Spring Boot ile yazilmis e-commerce demo projectidir. Kod GitHub-da gorune biler, deploy edildikden sonra ise linkle sayt kimi acila biler.

## Tech Stack

- Java 17
- Spring Boot
- Thymeleaf
- Maven

## Local Run

```powershell
cd C:\Users\Acer\IdeaProjects\untitled11
mvn spring-boot:run
```

Brauzerde ac:

```text
http://localhost:8081
```

## GitHub-a Yuklemek

Yeni public repository yarat ve bu command-lari islet:

```powershell
cd C:\Users\Acer\IdeaProjects\untitled11
git init
git add .
git commit -m "Initial commit"
git branch -M main
git remote add origin https://github.com/SENIN_USERNAME/lumicart.git
git push -u origin main
```

Bu zaman:
- kodun GitHub-da gorunecek
- dostlarin repository linkine baxib koda gire bilecek

## Sayt Kimi Acmaq

Tək GitHub sayti acmir. Bunun ucun deploy lazimdir.

En rahat variant:
- repo-nu GitHub-a yukle
- Render veya Railway-da `Deploy from GitHub` sec
- repository-ni bagla
- build command: `mvn clean package`
- start command: `java -jar target/ecommerce-platform-1.0.0.jar`

Deploy bitenden sonra public link alacaqsan:

```text
https://senin-project-adin.onrender.com
```

Bu linki dostlarina gondere bilersen. Onlar sayt kimi acib baxacaqlar.

## Qeyd

`application.properties` faylinda:

```properties
server.port=${PORT:8081}
```

qoyulub ki, hosting platformasi verdiyi portla islesin, lokalda ise 8081 qalir.
