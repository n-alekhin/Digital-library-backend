FROM stage2
COPY downloadModels.py .
RUN python3 -m spacy download en_core_web_sm
RUN python3 ./downloadModels.py
RUN rm ./downloadModels.py
