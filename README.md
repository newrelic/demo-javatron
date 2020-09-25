[![New Relic Experimental header](https://github.com/newrelic/opensource-website/raw/master/src/images/categories/Experimental.png)](https://opensource.newrelic.com/oss-category/#new-relic-experimental)

# Demo Javatron

![Test](https://github.com/newrelic/demo-javatron/workflows/Test/badge.svg?event=push)

A java tron for the `demo` platform.

Javatron is compatible with the [demo-deployer](https://github.com/newrelic/demo-deployer).

It can be deployed with a similar configuration, and can participate in a tron chain with other trons like itself or other languages.
The simulator can also be used to drive traffic to Javatron.


### Requirement

When hosting on a physical host or VM, javatron requires at least 700MB of memory.
When deployed with the deployer, a `memmon` watchdog process ensures the process is recycled if the memory consumption exceed this threshold.


### Behaviors

Javatron supports the below behaviors. For more information, see the [Behavior Documentation](https://github.com/newrelic/demo-deployer/tree/main/documentation/developer/behaviors)

* Throw
* Compute
* Memory Allocation


### Running with Docker

You can run Javatron with the provided Dockerfile at the root.
After ensuring your docker application is started on your machine, run the below commmand while in the root of the repository

Building the docker image
```bash
docker build -t javatron .
```

Running Javatron on the default port of 8081
```bash
docker run -it -p 8081:8081 javatron
```

Running the unit tests (handled with maven)
```bash
docker run -it --entrypoint mvn javatron test
```

### Deploy with demo-deployer
This java application can be deployed with the [demo-deployer](https://github.com/newrelic/demo-deployer) using the /deploy scripts in this repository.
Here is an example of the deploy config that can be used to deploy a javatron service on an AWS/EC2 instance:

```json
{
  "services": [
    {
      "id": "java1",
      "source_repository": "https://github.com/newrelic/demo-javatron.git",
      "deploy_script_path": "deploy/linux/roles",
      "port": 5001,
      "destinations": ["host"]
    }
  ],

  "resources": [
    {
      "id": "host",
      "provider": "aws",
      "type": "ec2",
      "size": "t2.micro"
    }
  ]
}
```

The deploy scripts are using Tomcat, and due to its installation resides in the /opt location of the host. Therefore, only 1 instance of Javatron can be deployed per host. There is currently no validations nor assertions that ensure this incorrect configuration is used.

Tomcat is configured with a max heap memory size of 700MB. A memory watchdog process `memmon` is implemented to recycle the process once that threshold (polling once per min).

#### Newrelic instrumentation

Javatron can be instrumented with newrelic. To do so you can reference the instrumentation role using the deployer configuration below.
Note, you'll also need to install the ansible galaxy plugin locally before running the deployer, using the command: `ansible-galaxy install newrelic.newrelic_java_agent`

```json
{

  "instrumentations": {
    "services":[
      {
        "id": "nr_agent_java",
        "service_ids": ["java1"],
        "provider": "newrelic",
        "source_repository": "https://github.com/newrelic/demo-newrelic-instrumentation.git",
        "deploy_script_path": "deploy/java/linux/tomcat/roles"
      }
    ]
  }

}
```


#### Log support

At this time Javatron only supports regular log with NR, not Logs-in-Context. To ship the logs to NR, use a regular log instrumentor, here is a snippet example:

```json
{

  "instrumentations": {
    "services":[
      {
        "id": "nr_log_javatron",
        "service_ids": ["java1"],
        "provider": "newrelic",
        "source_repository": "https://github.com/newrelic/demo-newrelic-instrumentation.git",
        "deploy_script_path": "deploy/logging/roles"
      }
    ]
  }

}
```


#### Cron jobs support

Cron jobs can be registered upon deployment using the demo-deployer Files configuration for the service. Here is an example for restarting a `java1` service every hour, at the 0 minute.

```json
{
  "services": [
    {
      "id": "java1",
      "display_name": "Java1",
      "source_repository": "https://github.com/newrelic/demo-javatron.git",
      "deploy_script_path": "deploy/linux/roles",
      "port": 5001,
      "destinations": ["host"],
      "files": [
        {
          "destination_filepath": "javatron/cronjob.json",
          "content": [
              {
                  "frequency": "0 * * * *",
                  "job": "/usr/bin/supervisorctl restart java1",
                  "root": true
              }
          ]
        }
      ]
    }
  ]
}
```


## Contributing
We encourage your contributions to improve `demo-javatron`! Keep in mind when you submit your pull request, you'll need to sign the CLA via the click-through using CLA-Assistant. You only have to sign the CLA one time per project.

If you have any questions, or to execute our corporate CLA, required if your contribution is on behalf of a company,  please drop us an email at opensource@newrelic.com.

**A note about vulnerabilities**

As noted in our [security policy](../../security/policy), New Relic is committed to the privacy and security of our customers and their data. We believe that providing coordinated disclosure by security researchers and engaging with the security community are important means to achieve our security goals.

If you believe you have found a security vulnerability in this project or any of New Relic's products or websites, we welcome and greatly appreciate you reporting it to New Relic through [HackerOne](https://hackerone.com/newrelic).

## License
`demo-javatron` is licensed under the [Apache 2.0](http://apache.org/licenses/LICENSE-2.0.txt) License.
