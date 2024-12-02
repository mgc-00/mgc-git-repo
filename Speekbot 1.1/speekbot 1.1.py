# Chatanooga Chatbot TTS/STT
# @author M Currie 
# @version 1.1 Nov 2024

# Import necessary libraries
import speech_recognition as sr
import pyttsx3
import transformers
import numpy as np
import datetime
import os
from torch.utils.data import Dataset, DataLoader
import torch

# Custom dataset class
class ChatDataset(Dataset):
    def __init__(self, encodings):
        self.encodings = encodings

    def __getitem__(self, idx):
        item = {key: torch.tensor(val[idx]) for key, val in self.encodings.items()}
        return item

    def __len__(self):
        return len(self.encodings.input_ids)

# The AI's build
class ChatBot():
    def __init__(self, name):
        print("----- Starting up", name, "-----")
        self.name = name
        
        # Set up pyttsx3 engine for TTS
        self.engine = pyttsx3.init()

        # Available voices / set default voice
        voices = self.engine.getProperty('voices')

        # Debugging: Print available voices and info
        print("Available voices:")
        for index, voice in enumerate(voices):
            print(f"Voice {index}: {voice.name}, ID: {voice.id}, Language: {voice.languages}, Gender: {voice.gender}, Age: {voice.age}")

        # Set voice (Voice number: 0 Susan - U.K., 1 - David - U.S., 2 Zira - U.S., 3 Haemi - Korean)
        self.set_voice(voices[0].id)

        # Set default speech rate (increase or decrease to modify speed)
        self.engine.setProperty('rate', 180)

        # Set default volume (0.0 to 1.0)
        self.engine.setProperty('volume', 1.0)  # Set volume to 100%

        # Test TTS system on startup
        self.text_to_speech("This is a test for text-to-speech using the selected voice. Can you hear me loud and clear? Please wait two minutes to begin, after the training data has been updated.", initial=True)

    def speech_to_text(self):
        recognizer = sr.Recognizer()
        with sr.Microphone() as mic:
            print("Listening...")
            
            # Optional: Adjust recognizer sensitivity if it's not detecting speech
            recognizer.adjust_for_ambient_noise(mic, duration=1)
            
            audio = recognizer.listen(mic)
            self.text = "ERROR"
        try:
            self.text = recognizer.recognize_google(audio)
            print("Me  --> ", self.text)
        except sr.UnknownValueError:
            print("Me  -->  ERROR: Google Speech Recognition could not understand audio")
        except sr.RequestError as e:
            print(f"Me  --> ERROR: Could not request results from Google Speech Recognition service; {e}")
        except Exception as e:
            print(f"Me  --> ERROR: {str(e)}")

    def text_to_speech(self, text, initial=False):
        print("Chatanooga --> ", text)
        
        self.engine.say(text)
        self.engine.runAndWait()  # Ensures the engine waits until the speech is completed

    def set_voice(self, voice_id):
        self.engine.setProperty('voice', voice_id)

    def wake_up(self, text):
        return True if self.name.lower() in text.lower() else False

    @staticmethod
    def action_time():
        return datetime.datetime.now().time().strftime('%H:%M')

# Running the AI
if __name__ == "__main__":
    ai = ChatBot(name="Chatanooga")
    
    # Set the voice to the third voice in the list (index 2)
    ai.set_voice(ai.engine.getProperty('voices')[2].id)

    # Load the language model and tokenizer (DialoGPT) from transformers (done outside the loop for performance)
    model = transformers.AutoModelForCausalLM.from_pretrained("microsoft/DialoGPT-medium")
    tokenizer = transformers.AutoTokenizer.from_pretrained("microsoft/DialoGPT-medium")
    
    os.environ["TOKENIZERS_PARALLELISM"] = "true"
    
    # Trainer for expanding vocabulary
    new_conversations = [
        {"input_text": "How's the weather today?", "response_text": "It's sunny and warm."},
        {"input_text": "What's your favorite book?", "response_text": "I enjoy many books, but one of my favorites is 1984 by George Orwell."},
        {"input_text": "Tell me a joke.", "response_text": "Why don’t scientists trust atoms? Because they make up everything!"},
        {"input_text": "What's the capital of France?", "response_text": "The capital of France is Paris."},
        {"input_text": "How are you today?", "response_text": "I'm doing great, thank you! How about you?"},
        {"input_text": "What's your favorite movie?", "response_text": "It's hard to pick just one, but I love The Matrix."},
        {"input_text": "Can you tell me a fun fact?", "response_text": "Sure! Did you know that honey never spoils? Archaeologists have found pots of honey in ancient Egyptian tombs that are over 3,000 years old and still perfectly edible."},
        {"input_text": "Who is the current president of the United States?", "response_text": "As of now, the president of the United States is Joe Biden."},
        {"input_text": "What is the speed of light?", "response_text": "The speed of light in a vacuum is approximately 299,792 kilometers per second (or about 186,282 miles per second)."},
        {"input_text": "How do I cook pasta?", "response_text": "To cook pasta, boil a pot of water with a pinch of salt, add the pasta, and cook it according to the package instructions until it is al dente. Drain the pasta and serve with your favorite sauce."},
        # Add more training examples as needed
    ]

    # Set padding token
    tokenizer.pad_token = tokenizer.eos_token
    
    # Tokenize training data
    train_texts = [conv["input_text"] + tokenizer.eos_token + conv["response_text"] for conv in new_conversations]
    train_encodings = tokenizer(train_texts, return_tensors='pt', max_length=512, truncation=True, padding=True)
    
    # Create dataset
    train_dataset = ChatDataset(train_encodings)

    # Custom training loop
    def train(model, dataset, tokenizer, epochs=5, batch_size=4):
        model.train()
        train_loader = DataLoader(dataset, batch_size=batch_size, shuffle=True)
        optimizer = torch.optim.AdamW(model.parameters(), lr=3e-5)

        for epoch in range(epochs):
            print(f"Epoch {epoch + 1}/{epochs}")
            for batch in train_loader:
                optimizer.zero_grad()
                inputs = {key: value.to(model.device) for key, value in batch.items()}
                outputs = model(**inputs, labels=inputs['input_ids'])
                loss = outputs.loss
                loss.backward()
                optimizer.step()
                print(f"Loss: {loss.item()}")

    # Train the model
    train(model, train_dataset, tokenizer, epochs=5, batch_size=4)

    # Save the model
    model.save_pretrained("./my_trained_model")
    tokenizer.save_pretrained("./my_trained_model")

    ex = True
    while ex:
        ai.speech_to_text()

        ## wake up
        if ai.wake_up(ai.text) is True:
            res = "Hello I am Chatanooga the AI, what can I do for you?"
        
        ## action time
        elif "time" in ai.text:
            res = ai.action_time()
        
        ## respond politely
        elif any(i in ai.text.lower() for i in ["thank", "thanks", "thank you very much!"]):
            res = np.random.choice(["You're welcome!", "Anytime!", "No problem!", "Cool!", "I'm here if you need me!", "Don't mention it!"])
        
        elif any(i in ai.text.lower() for i in ["exit", "close"]):
            res = np.random.choice(["See you later!", "Have a great day", "Bye bye!", "Goodbye", "Hope to hear from you again soon", "take care!"])
            ex = False
        ## conversation
        else:   
            if ai.text == "ERROR":
                res = "Sorry, come again?"
            else:
                # Tokenize input and generate a response using DialoGPT
                inputs = tokenizer.encode(ai.text + tokenizer.eos_token, return_tensors="pt")
                reply_ids = model.generate(inputs, max_length=1000, pad_token_id=tokenizer.eos_token_id)
                res = tokenizer.decode(reply_ids[:, inputs.shape[-1]:][0], skip_special_tokens=True)

        ai.text_to_speech(res)
    
    print("----- Closing down Chatanooga -----")
