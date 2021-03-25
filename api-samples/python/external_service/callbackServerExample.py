from flask import Flask, jsonify, request

# initialize our Flask application
app= Flask(__name__)

# implements the callback method to be able to receive callbacks from EOPORT when products are ready
@app.route("/callback", methods=["POST"])
def callback():
    posted_data = request.get_json()
    subscriptionID = posted_data['subscription']
    print(str("Successfully received notification for subscription " + subscriptionID + " download link is " + posted_data['downloadLink']))
    # get the download url
    # the download url comes with the credentials attached to no need to authenticate
    downloadurl = posted_data['downloadLink']
    # TODO - create a task to download the product from the notification and do something
    # r = requests.get(url, allow_redirects=True)
    # open('downloadfile', 'wb').write(r.content)
    return jsonify()

# only for debug purpose
@app.route("/check", methods=["GET"])
def message():
    return jsonify("All is fine")

#  main thread of execution to start the server
if __name__=='__main__':
    app.run(debug=True)
