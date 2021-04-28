from flask import Flask, jsonify, request

# initialize our Flask application
app= Flask(__name__)

@app.route("/callback", methods=["POST"])
def callback():
    posted_data = request.get_json()
    subscriptionID = posted_data['subscription']
    print(str("Successfully received notification for subscription " + subscriptionID + " download link is " + posted_data['downloadLink']))
    return jsonify()

@app.route("/check", methods=["GET"])
def message():
    return jsonify("All is fine")

#  main thread of execution to start the server
if __name__=='__main__':
    app.run(debug=True)

