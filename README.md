Redacts the contents of password parameters from the console log.

This works with Freestyle and Pipeline jobs.

For example, imagine the following Pipeline code;

    pipeline {
      agent any

      parameters {
        string(name: 'YOUR_USERNAME', defaultValue: 'Mr Jenkins')
        password(name: 'YOUR_PASSWORD', defaultValue: 'secret')
      }

      stages {
        stage('Example') {
          steps {
            echo "Hello ${params.YOUR_USERNAME}"
            echo "Your password is ${params.YOUR_PASSWORD}"
            echo "secret"
          }
        }
      }
    }

The following would get printed to the console.

    [Pipeline] echo
    Hello Mr Jenkins
    [Pipeline] echo
    Your password is ****
    [Pipeline] echo
    ****
