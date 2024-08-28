FROM eclipse-temurin:21-jdk-jammy
WORKDIR /app

RUN apt-get update && apt-get install -y r-base
RUN R CMD javareconf
RUN R -e "install.packages('rJava',dependencies=TRUE, repos='http://cran.rstudio.com/', verbose=TRUE)"
RUN R -e "library(rJava)"

ENV LD_LIBRARY_PATH=/usr/local/lib/R/site-library/rJava/jri
COPY target/STRAIT.jar /app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]
