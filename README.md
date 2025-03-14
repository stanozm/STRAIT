# STRAIT

STRAIT is an automated software reliability analysis tool that works from the command line.
It can be used to fit software reliability growth models onto software project time - cumulative issue amount data.

The tool accepts software project issue report data as input.
Currently GitHub, Jira and Bugzilla issues are supported.

Reliability analysis reports are produced as output.

STRAIT was proposed in paper http://dx.doi.org/10.1109/MSR.2019.00025 on *2021 IEEE/ACM 18th International Conference on Mining Software Repositories (MSR)*
The tool was developed by Radoslaw Micko as part of his [**Bachelor's thesis**](https://is.muni.cz/th/a2htp/) and extended in his Master's thesis.
The tool was later extended and refactored by Valtteri Valtonen as part of his Master's thesis.
This is the two times extended and refactored version of STRAIT.

## Building the tool

In order to use STRAIT, you need to build the .jar file from the project source code.
This can be done using the Maven package action.

Before building, make sure that your environment fulfills the following requirements:

* Java JDK version 21: [https://www.java.com/en/](https://www.java.com/en/)
* R Project, version 4.3.1: [https://cloud.r-project.org/](https://cloud.r-project.org/)
* Apache Derby DB, version 10.16.1.1: [https://db.apache.org/derby](https://db.apache.org/derby/papers/DerbyTut/install_software.html#derby)

The tool has been tested to work with the versions mentioned above, but the version requirements may not be absolute.

Follow the following installation and running instructions for the dependencies:

**Installation for MS Windows**

1. If Java is not installed, run the Java installer
2. Append Java bin directory to the environment PATH variable (e.g., C:\Program FilesJava\jdk1.8.0\_171\bin)
3. If R Project is not installed, run the installer
4. Run the R.exe (as administrator) and in the console, install the packages: 
    * install.packages("rJava")
    * install.packages("nls2")
    * install.packages("broom")
    * install.packages("aomisc")
5. Set the environment variables for R Project:
    *  R_HOME=Path-to-R-install-directory (e.g., R_HOME=C:\Program Files\R-3.5.)
    *  path=R_HOME\bin\x64
    *  path=R_HOME\library\rJava\libs\x64
    *  path=R_HOME\library\rJava\jri\x64
6. Make sure Apache Derby client server is running or run - *startNetworkServer.bat* (not necessary if you are using the docker setup)

If 32-bit operating system is used the *\x64* part should be replaced with *\i386*.

**Installation for Unix**

The Unix installation assumes that the Aptitude package
manager is available. The following commands should be
executed via terminal:

1. sudo apt-get install default-jdk
2. sudo apt install dirmngr apt-transport-https ca-certificates software-properties-common gnupg2
3. sudo apt-key adv --keyserver keyserver.ubuntu.com --recv-key 'E298A3A825C0D65DFD57CBB651716619E084DAB9'
4. sudo add-apt-repository 'deb https://cloud.r-project.org/bin/linux/ubuntu jammy-cran40/'
5. sudo apt update
6. sudo apt install r-base
7. sudo -i R
    * install.packages("rJava")
    * install.packages("nls2")
    * install.packages("broom")
    * install.packages("aomisc")
8. Set the R_HOME variable
9. Make sure Apache Derby client server is running or run - *startNetworkServer* (not necessary if you are using the docker setup)

Once the instructions above are completed, 
you may add a git_hub_authentication_file.properties file to the project folder.
This file should include your Github credentials as well as the Github api token that starts with ghp_ .
It should look something like this:
```
name=my_github_name
password=my_github_password
token=ghp_XXXX...
```

At this point, the Maven package action should complete. 
The resulting .jar file can be used to perform reliability analysis. 
You may need to edit your Java VM options, if an JRI not found error appears. The option for this is 
```-Djava.library.path="/Library/Frameworks/R.framework/Resources/library/rJava/jri/"``` on a Mac computer.

>Performing the build process is probably simplest by using IntelliJ Idea Community edition. 
>The IDE offers Maven integration, and allows running the STRAIT Core class directly without the package action.
> STRAIT usage options can then be provided in the run configuration.

## Usage

The tool can be executed from command-line by running:

```java -jar strait.jar [OPTIONS]```

An overview of command-line options is in Table - options. The tool also prints a
list of all options if no argument is provided. The help can be accessed by running:

```java -jar strait.jar --help```

A simple execution of the tool to evaluate the testify
project hosted at GitHub may look like:

```java -jar strait.jar -url https://github.com/stretchr/testify -e -fde -fc -fdu -ft 2018-01-01T00:00:00 2021-01-01T00:00:00```

> With the *-url* option, it specifies the location of the project.  The option *-e* starts the execution of the SRGM analysis. No specific models are selected, so all the available SRGMs will be applied. The *-fde* will filter only defects from issue reports. With the option *-fc*, closed issues are only concidered. The *-fdu* option filters out duplicated issues. Furthermore, with *-ft* it limits the time period for which issue reports will be considered.

### Docker setup

The tool can also be run in a Docker container. One of the benefits is that you do not need to manually run a client server.

**Setup**

1. You need to have Docker installed and running on your machine.
2. Build an image from the Dockerfile in the core module folder by running the following command:
```docker build -t strait-service:latest Core```

**Running the analysis**

1. Edit the analysis.txt file in the project root folder to specify the analysis options the same way you would do in 
the command line. You can run multiple analyses at the same time each defined in a separate line.
For example:
```
-url https://github.com/stretchr/testify -ms mo -e -fde -fc -fdu -ft 2018-01-01T00:00:00 2019-01-01T00:00:00
-url https://github.com/PRML/PRMLT -ms du -e -fde -fc -fdu -ft 2018-01-01T00:00:00 2019-01-01T00:00:00
```
2. Run the script in the project root folder to start the analysis:
```.\run_analysis.sh```


## Run modes

This version of STRAIT supports several different run modes. 
Included are a batch analysis mode, 
a single project analysis mode, 
a snapshot analysis mode as well as some informative modes.

### Single project analysis mode

This analysis mode allows you to perform reliability analysis on a single project.
This mode is used when the -url option is applied. 

For example ```java -jar strait.jar -url https://github.com/stretchr/testify -e -fde -fc -fdu -ft 2018-01-01T00:00:00 2021-01-01T00:00:00```.

This performs reliability analysis for the single project. 
A HTML report file is produced as output.
You may also specify Bugzilla or Jira CSV files in place of the GitHub URL.
Currently only GitHub issue reports can be downloaded automatically from the source repository based on the URL.
Jira and Bugzilla issues require CSV files that have been cleaned of extra columns.
Examples of acceptable files are located in the exampleinput folder of the repository.

This mode saves an issue snapshot of the target project to the database by default, 
if the project is not yet in the database and the issues are collected from GitHub. 
On future runs, the snapshot data for the project is used automatically.
If you want to overwrite snapshots in the database, use the ```-odb``` option.
If you do not want to use the database at all use the ```-ndb``` option.

### Batch analysis mode

This analysis mode allows analyzing several projects in a single run. 
This mode is used when option ```-bcf``` with a batch configuration json file path is applied.

For example ```-bcf batchconfig.json -fde -fc -ms ll --evaluate --solver ls```.

This mode acts like the single project analysis mode, except it also produces a CSV report of the analysis results.

The batch analysis mode configuration JSON file looks like this:
```
{
"dataSources": [
   {"type": "github", "location": "https://github.com/novuhq/novu"},
   {"type": "bugzilla", "location": "./Eclipse-jdt 2.0.csv"},
   {"type": "jira", "location": "./ONOS-1.10.csv"}
]}
```
### Snapshot analysis mode

This analysis mode allows for performing a single project analysis using single snapshot data 
identified by the snapshot name. This mode is used when option ```-sn``` is applied with an existing snapshot name.

For example ```java -jar strait.jar -sn https://github.com/stretchr/testify -e -fde -fc -fdu -ft 2018-01-01T00:00:00 2021-01-01T00:00:00```.

This mode is very much like the single project analysis mode. 
It is mainly useful for performing analyses with snapshots with custom names.
In this STRAIT version these names can be provided by modifying the database. 
By default, the project URL is used as the snapshot name.

### Other modes

In addition to the previously mentioned modes, certain other informative modes are also available.
You can list all snapshots in the database, as well as list all snapshots for a certain URL. 
Options for these are available in the options table.

# Table - options

This table lists some of the available STRAIT options. 
A more comprehensive list can be seen with the Help command.

| Short option | Long option | Arguments |
| :---: | :---: | :---: |
| -h | --help | - |
| -url | - | [Repository URL] |
| -asl | --allSnapshotsList | - |
| -sn | --snapshotName | - |
| -cf | - |  [Path to config file]|
| -sl | --snapshotsList | - |
| -e | --evaluate | [Output file name] |
| -p | --predict | [Number of time units for prediction] |
| -fl | --filterLabel | [Label names] |
| -fc | --filterClosed | - |
| -ft | --filterTime | [From] [To] |
| -fde | --filterDefects | - |
| -fdu | --filterDuplications | - |
| -ms | --models | [Models] |
| -pt | --periodOfTesting | [Testing period time unit] |
| -tb | --timBetweenIssues | [Time unit for TBF] |
| -gm | --graphMultiple | - |
| -so | --solver | [Solver] |
| -out | - | [Output type] |
