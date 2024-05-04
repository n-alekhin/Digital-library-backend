from flask import Flask, request, jsonify
from sentence_transformers import SentenceTransformer
import numpy as np
import spacy
import torch

app = Flask(__name__)
device = "cuda" if torch.cuda.is_available() else "cpu"
model = SentenceTransformer('sentence-transformers/all-mpnet-base-v2', device=device)
nlp = spacy.load("en_core_web_sm")

def my_function(text):
    vector = model.encode(text)
    # Преобразование вектора в список
    vector_list = vector.tolist() if isinstance(vector, np.ndarray) else vector
    return vector_list

@app.route('/', methods=['POST'])
def handle_request():
    data = request.get_json()  # Получение данных из тела запроса
    input_string = data.get('input', '') if data else ''
    #result = my_function(input_string)
    result = [0] * 784
    return jsonify({'result': result})

@app.route("/NLP", methods=["POST"])
def NLP():
    data = request.get_json()
    text = data.get("input", "") if data else ''
    vector_search = data.get("vectorSearch", False) if data else False
    doc = nlp(text)
    noun_chunks = set(chunk.text for chunk in doc.noun_chunks)
    nouns = set(token.text for token in doc if token.pos_ == "NOUN")

# Объединяем существительные группы и отдельные существительные
    all_nouns = noun_chunks.union(nouns)
    print(all_nouns)
    filtered_phrases = []
    for chunk in all_nouns:
        if vector_search:
            words = [token.lemma_ for token in nlp(chunk) if token.pos_ == "NOUN"]
            if words:
                filtered_phrases += words
        else:
            words = [token.lemma_ for token in nlp(chunk) if token.pos_ not in ["DET", "PRON"]]
            if words:
                filtered_phrases.append(" ".join(words))
    print(filtered_phrases)
    x = set(filtered_phrases)
    answer = list(x)
    print(answer)
    return jsonify({'noun_chunks': answer})


if __name__ == '__main__':
    #model = SentenceTransformer('sentence-transformers/paraphrase-multilingual-MiniLM-L12-v2')
    app.run(debug=True)
