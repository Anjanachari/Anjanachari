FROM python:3.10-slim

WORKDIR /app

COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt
RUN python -c "import flask; print('Flask version:', flask.__version__)"

COPY . .

EXPOSE 5000

CMD ["python", "app.py"]
