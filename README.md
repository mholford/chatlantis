# chatlantis
Chatlantis is a framework for designing chatbots.

It offers some cool advantages:
- **Customizable Utterance processing workflow**: Process user utterances in a pipeline of discrete stages.  Possible stages include Entity Extraction (implemented), POS tagging, sentiment detection.  Chatlantis' workflow allows for multipe interpretations of utterances to be considered before selecting an optimal response
- **Immutable Data Structures**: Utterances and their Contexts are immutable.  This allows for utterance processing to occur in parallel, distributed environments without introducing concurrency issues.  The Context object is a fully immutable, arbitrarily deep nested map structure built on "persistent" collections.  (We use the excellent pcollections library).
- **Constraint Driven Flow Logic**: Conversational Intents are resolved by programmatic resolution of Constraints.  Intents and Constraints are specified declaratively.
- **Sentence Generation framework to match user utterances**: Possible utterances can be declared modularly; they can be built from smaller units of phrases.  The framework computes legal permutations of the sentence and feeds these to a lookup table.  Placeholders for wildcards and extracted entites are supported.  A central lookup table matches utterances to instructions to be performed against the Context.
- **All FSTs, all the time**: FSTs (finite state transducers) provide blazingly fast lookup of string keys.  We use FSTs as the data structure driving both Entity Extraction and Utterance matching.

Chatlantis is very much a work in progress.  There is a small sample working app that can be found in the test cases.  The app mimics a ticket-maintenance chatbot which interacts with a ticketing system like JIRA.
