# Service samples
The projects in this directory illustrate how to write a service so that it can be registered and called within EOPORT.

The service will interface with the EOPORT Production Manager (PM). There are three interactions with the PM:
* scheduling. A POST request from the PM to the service. Called a few minutes before the processing so the service can prepare the computing resources in advance. This is an optional end point.
* processing. A POST request from the PM to the service. Called when data is available for processing. The POST payload contains the information for the input and output.
* results. A POST request from the service to the PM. Called when the data has been processed and the product is stored on an S3 bucket. The result response should include the usage report so that the customers can be billed.

Once you created and uploaded your code to an EOPORT tenant, you will have to register a service using the service provider interface @ https://webapp.dev02.eoport.eu/Supplier.html. If you do not have a service provider account yet, please contact us @ support@eoport.com.

To register a service, please use the service URL @ https://webapp.dev02.eoport.eu/Supplier.html#processes: and click on "Add New Service" button. Follow the instructions and save your service.

As soon as you have made your service public, customers will be able to create subscriptions. When a subscription is active and new data is available for the subscription, your service will be called at the implemented end points.
  