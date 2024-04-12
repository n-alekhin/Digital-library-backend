FROM python:3.10.6 as stage1

# Install Base PyTorch System - Assume CPU
RUN pip3 install \
  torch \
  torchvision \
  torchaudio \
  torchdatasets \
  torchtext \
  datasets \
  transformers \
  --extra-index-url https://download.pytorch.org/whl/cpu \