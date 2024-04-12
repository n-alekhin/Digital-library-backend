FROM stage1

WORKDIR /server

COPY requirements.txt requirements.txt
RUN pip3 install -r requirements.txt