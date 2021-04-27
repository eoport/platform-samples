from flask import Flask, jsonify, request

from com.obs.client.obs_client import ObsClient

from osgeo import gdal, osr, ogr

# initialize our Flask application
app= Flask(__name__)


def download_data(objectURI):
    # Create an instance of ObsClient.
    obsClient = ObsClient(
        access_key_id='*** Provide your Access Key ***',
        secret_access_key='*** Provide your Secret Key ***',
        server='yourdomainname'
    )
    # Use the instance to access OBS.
    resp = obsClient.getObject('bucketname', objectURI)
    if resp.status < 300:
        print('requestId:', resp.requestId)
        if resp.body and resp.body.response:
            while True:
                chunk = resp.body.response.read(65536)
                if not chunk:
                    break
            print(chunk)
            resp.body.response.close()
        else:
            print('errorCode:', resp.errorCode)
            print('errorMessage:', resp.errorMessage)
    # Close obsClient.
    obsClient.close()

@app.route("/service", methods=["POST"])
def service():
    posted_data = request.get_json()
    objectURI = posted_data["objectURI"]
    downstreamURI = posted_data["downstreamURI"]
    metadata = posted_data["metadata"]
    subscriptionID = posted_data["subscriptionID"]
    # download the data
    file = download_data(objectURI)

@app.route("/scheduler", methods=["POST"])
def scheduler():
    posted_data = request.get_json()

#  main thread of execution to start the server
if __name__=='__main__':
    app.run(debug=True)
