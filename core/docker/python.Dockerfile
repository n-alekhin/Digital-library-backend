FROM stage3
COPY main.py .
CMD [ "python3", "-m" , "flask", "run", "--host=0.0.0.0", "-p", "5000"]