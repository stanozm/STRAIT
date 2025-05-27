FROM eclipse-temurin:21-jdk-jammy
WORKDIR /app

RUN apt update && apt install -y dirmngr apt-transport-https ca-certificates software-properties-common gnupg2
RUN apt-key adv --keyserver keyserver.ubuntu.com --recv-keys E298A3A825C0D65DFD57CBB651716619E084DAB9
RUN add-apt-repository 'deb https://cloud.r-project.org/bin/linux/ubuntu jammy-cran40/'
RUN apt update && apt install -y \
    r-base-core=4.4.1-1.2204.0 \
    r-base-dev=4.4.1-1.2204.0 \
    libssl-dev \
    libcurl4-openssl-dev \
    cmake
RUN R CMD javareconf
RUN R -e "install.packages('rJava',dependencies=TRUE, repos='https://cran.rstudio.com/', verbose=TRUE)"
RUN R -e "install.packages('nls2',dependencies=TRUE, repos='https://cran.rstudio.com/', verbose=TRUE)"
RUN R -e "install.packages('broom',dependencies=TRUE, repos='https://cran.rstudio.com/', verbose=TRUE)"
RUN R -e "install.packages('statforbiology',dependencies=TRUE, repos='https://cran.rstudio.com/', verbose=TRUE)"

ENV R_HOME=/usr/lib/R
ENV LD_LIBRARY_PATH=/usr/local/lib/R/site-library/rJava/jri
COPY target/STRAIT.jar /app.jar
