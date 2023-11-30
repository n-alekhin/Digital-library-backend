from flask import Flask, request, jsonify
from sentence_transformers import SentenceTransformer
import numpy as np

app = Flask(__name__)
model = None

def my_function(text):
    vector = model.encode(text)
    # Преобразование вектора в список
    vector_list = vector.tolist() if isinstance(vector, np.ndarray) else vector
    return vector_list

@app.route('/', methods=['POST'])
def handle_request():
    data = request.get_json()  # Получение данных из тела запроса
    input_string = data.get('input', '') if data else ''
    result = my_function(input_string)
    return jsonify({'result': result})

if __name__ == '__main__':
    model = SentenceTransformer('sentence-transformers/paraphrase-multilingual-MiniLM-L12-v2')
    app.run(debug=True)
