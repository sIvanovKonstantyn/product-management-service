FROM openjdk:17
ADD build/libs/product-management-0.0.1-SNAPSHOT.jar product-management.jar
ENTRYPOINT ["java", "-jar","product-management.jar"]
EXPOSE 8080
