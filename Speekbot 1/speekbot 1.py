# Chatanooga Chatbot TTS/STT
# @author M Currie 
# @version 1.0

# Import necessary libraries
import speech_recognition as sr
import pyttsx3
import transformers
import numpy as np
import datetime
import os


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
        self.text_to_speech("This is a test for text-to-speech using the selected voice. Can you hear me loud and clear? Please respond in 8 seconds.", initial=True)

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
