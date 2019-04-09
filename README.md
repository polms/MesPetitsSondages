# Mes Petits Sondages

## Application architecture

The application is composed of:

* `xxx` class: 
  
* 
  
## Build and execution

The application is distributed as a Maven project, composed of a `src/` directory that contains the java sources and a `pom.xml` file that contains the Maven project description

### Project build

To build the project:

    mvn install
	
### Application execution

To launch the application:

    java -jar target/sondages-1.0.jar
    
You can then launch separately each part of the application:

	(sounder|answerer|analyzer) [args]
	
To launch the sounder :

	sounder <jms_host> <jms_port>
	
To launch the answerer :

	answerer <js_server_host> <js_server_port> <jms_server_host> <jms_server_port>
	
To launch the analyzer :

	analyzer <js_server_host> <js_server_port> <jms_server_host> <jms_server_port>
