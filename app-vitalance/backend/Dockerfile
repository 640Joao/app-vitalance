# 1. Usar imagem oficial do Java 21
FROM eclipse-temurin:21-jdk-alpine

# 2. Pasta de trabalho
WORKDIR /app

# 3. Copiar apenas os arquivos essenciais
COPY . .

# 4. Dar permissão ao gradlew
RUN chmod +x gradlew

# 5. Construir o jar final
RUN ./gradlew clean bootJar --no-daemon

# 6. Expor a porta
EXPOSE 8082

# 7. Rodar o jar (Railway passa PORT como variável)
CMD java -jar build/libs/*.jar
