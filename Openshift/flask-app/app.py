from flask import Flask, render_template, request, redirect
from models import db, User

app = Flask(__name__)
app.config['SQLALCHEMY_DATABASE_URI'] = 'sqlite:///db.sqlite3'
app.config['SQLALCHEMY_TRACK_MODIFICATIONS'] = False
db.init_app(app)

@app.route('/init_db')
def init_db():
    db.create_all()
    return "Database initialized!"

@app.route('/')
def index():
    users = User.query.all()
    return render_template('index.html', users=users)

@app.route('/add', methods=['GET', 'POST'])
def add_user():
    if request.method == 'POST':
        name = request.form['name']
        if name:
            db.session.add(User(name=name))
            db.session.commit()
        return redirect('/')
    return render_template('add_user.html')

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000)
