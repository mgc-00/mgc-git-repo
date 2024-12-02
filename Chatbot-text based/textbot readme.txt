Text-Based Chatbot
Requirements

Python 3.7.4, VS Code

To get the text-based chatbot running, install the following libraries:
	1.	chatterbot: For the chatbot framework.
	pip install chatterbot

	2.	chatterbot_corpus: For pre-trained conversational data.
	pip install chatterbot_corpus
	
	3.	spacy: For natural language processing (NLP) tasks.
	pip install spacy
	
	4.	English Model for SpaCy: You also need to download the English model.
	python -m spacy download en_core_web_sm
	
Installation Command
	To install all necessary libraries in one go, use the following command:
	pip install chatterbot chatterbot_corpus spacy
	python -m spacy download en_core_web_sm
	
Running the Chatbot
Follow the specific instructions in your Python script to get the chatbot up and running. To run the chatbot, use the following command:
python text_chatbot.py

