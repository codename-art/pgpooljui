PGPoolJUI is a simple web ui for [PGPool](https://github.com/sLoPPydrive/PGPool/)
made with [Vaadin](https://vaadin.com/) and [Groovy](http://groovy-lang.org/).
No database changes required, working with already existing PGPool database.

## Account Summary
![image](https://user-images.githubusercontent.com/19952171/34322771-b4115662-e841-11e7-9865-d6acc682a7e7.png)

## Instance Summary
![image](https://user-images.githubusercontent.com/19952171/34322779-0b131c48-e842-11e7-9af4-efa78e41abdf.png)
## Ban Statistics by Day 
![image](https://user-images.githubusercontent.com/19952171/34322782-3f08025c-e842-11e7-847b-683f3b773812.png)
Allows you to return accounts to work, that was banned N days ago. Note, that it doesn't perform ban check.
After this action PGPool may can allocate them for RocketMap, and map shuold perfom checks  on its own.
![image](https://user-images.githubusercontent.com/19952171/34322784-6ab20be6-e842-11e7-9e77-40bfcdba9393.png)

## Usage
RocketMap database should be created first.
### Using prebuilded jar
1. Download latest [release](https://github.com/codename-art/pgpooljui/releases).
2. Create application.properties file with flowing fields:
```properties
server.port=8282
spring.datasource.url=jdbc:mysql://localhost:3306/pgo
spring.datasource.username=
spring.datasource.password=
```
3. Change `pgpool` in database url for your DB name, `username` and `password`.
4. Start with
```commandline
java -jar pgpooljui*.jar
```

### Using latest source code
1. `git clone`
2. Create application.properties same as above.
3. Build jar:
```commandline
./gradlew build
```
4. Start with:
```commandline
java -jar build/libs/pgpooljui*.jar
```