FROM openjdk

COPY *.jar /app.jar

EXPOSE 10000

ENTRYPOINT ["sh","-c","java -Dspring.profiles.active=prod -jar /app.jar"]