# Chatbot (text based)
# @author M Currie 
# @version 1.0

from chatterbot import ChatBot
from chatterbot.trainers import ListTrainer, ChatterBotCorpusTrainer

# Create a new instance of a ChatBot
bot = ChatBot("chatbot", read_only=False, 
    logic_adapters=[
        {
            "import_path": "chatterbot.logic.BestMatch",
            "default_response": "Sorry, I don't understand. Please repeat!",
            "maximum_similarity_threshold": 0.90
        }
    ]
)

# List of conversations for training the chatbot, add more to train
list_to_train = [
    "hi",
    "hi there",
    "what information can you give me?",
    "go to https://chatgpt.com/ for more info",
    "what's your name?",
    "I'm a chatbot",
    "how old are you?",
    "I'm ageless!",
    "how are you?",
    "I'm an emotionless machine",
    "What's your favorite food?",
    "Good information",
    "Are you happy?",
    "I don't have any emotions",
    "I feel tired today",
    "Maybe you should get some sleep!",
    "What's your job?",
    "To answer your questions"
]

list_to_train2 = [
    "hi",
    "what's up?",
    "how are you?",
    "I'm peachy, thanks!",
    "what's your name?",
    "I'm your assistant",
    "how old are you?",
    "infinity",
    "What's your favorite food?",
    "depends what mood I am in",
    "Are you happy?",
    "I am always happy, thanks",
    "I feel tired today",
    "reduce your caffeine intake then!",
    "What's your job?",
    "virtual dogsbody",
    "I don't know what you are talking about"
]

# Initialize the ListTrainer with the chatbot
list_trainer = ListTrainer(bot)

# Train the chatbot with the provided lists
list_trainer.train(list_to_train)
list_trainer.train(list_to_train2)

# Initialize the ChatterBotCorpusTrainer with the chatbot
trainer = ChatterBotCorpusTrainer(bot)

# Train the chatbot using the ChatterBot corpus data
trainer.train("chatterbot.corpus.english")

# Main loop to interact with the chatbot
while True:
    user_response = input("User:")
    print("TextBot: " + str(bot.get_response(user_response)))