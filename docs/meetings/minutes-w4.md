| Key          | Value                                                            |
|--------------|------------------------------------------------------------------|
| Date:        | 05/03/24                                                         |
| Time:        | 14.45-15.30                                                      |
| Location:    | Flux Hall B                                                      |
| Chair        | Esther                                                           |
| Minute Taker | Maurits                                                          |
| Attendees:   | Paras Khan, Esther Wit, Lasse Geurts, Billy Runne, Maurits Sloof |

Agenda Items:
-
- Opening by chair (1 min)
- Check-in: How is everyone doing? (1 min)
  - Paras: Doing good, just moved in.
  - Maurits: Also doing good.
  - Billy: Good, started with LA.
  - Lasse: Could be doing better.
  - Esther: Less chaotic than last week, so that's good.

- Announcements by the team (1 min)
  - None
  
- Approval of the agenda - Does anyone have any additions? (1 min)
  - Discuss text id format
  
- Approval of last minutes - Did everyone read the minutes from the previous 
  meeting? (2 min)
  - All good
  
- Have the tasks from last week been done? What have you worked on in the last 
  week (1 min)
  - Everything has been completed.
  - Everyone worked on:
    - Paras: Testing the participant controller;
    - Maurits: Bug fixing and improved last week's code; 
    - Billy: Testing and participant controller;
    - Lasse: Client UI;
    - Esther: The Event Controller.
  
- Announcements by the TA (3 min)
  - If you failed the knockout criteria, fix it
    - Explained the criteria like it was stated in the announcement on 
      Brightspace.

- Presentation of the current app to TA (5 min)
  - Showed the serverside implementation of the api
  - Showed the current UI

- Questions for TA (5 min)
  - Paras got a warning he didn't merge anything, even tho he did. 
    - Turns out he didn't approve and the warning criteria weren't properly 
      split, so he got the wrong warning message.
  - What should we do for testing the repository?
    - For testing of the repository, Mockito is a useful library for automating
      a lot of mock-ups needed for testing.
  - How should we divide functionality in the backend?
    - Data classes shouldn't contain too much functionality, put this in the 
      service. For example, don't make the api call to get the exchange rate in 
      the `Currency` class itself. 
  - What about the Code of Conduct feedback?
    - We can keep working on the code of conduct, this is just feedback. It will 
      be again graded in week 10.

- Talking Points: (Inform/ brainstorm/ decision-making/ discuss)
  - We should add at least five issues from now on (as were with five people) 
    and assign everyone at least one.
  - Milestone for this week (5 min)
    - We want to connect the front end to the back end.
    - Issues (partially elaborated and formalised in the meeting directly after 
      the meeting with the TA):
      - Issue 1: Map weak entities

      - Issue 2: Create/finish up
        - event overview window,
        - expense overview window,
        - participant-adding window
        (these are under the event overview window)
        - create event window
        - my-events window

      - Issue 3:
        - Create Expense controller
        - Write up test for Expense Controller
        - Write up test for Event Controller

      - Issue 4: Map the frontend buttons to backend with the proper RESTful request/response entity creation methods.
        - For the event overview window
        - For the expense overview window
        - For the participant-adding window

      - Issue 5: Refactor the Expense to Transaction.
        - Actual Refactor of the Expense class to Translation class with all resulting changes to that class.
        - Create a debt calculation algorithm.
        - Create/update the api related to debts and transactions.
  
- Tasks & Planning
  - Every issue needs estimated time, tags and spent time and maybe subissues 
   and weights.
    
- Discuss text id format
  - We settled on `PascalCase`.

- Summarize action points: Who, what, when? (5 min)
  - After the meeting we will create the issues and divide them.
  
- Feedback round: What went well and what can be improved next time? (2 min)
  - Maybe ask more question to the TA on mattermost instead of asking everything during 
    the meeting to free up some time during the meeting.
  
- Planned meeting duration != actual duration? Where/why did you mis-estimate? (2 min)
  - Everything was about lined up.

- Question round: Does anyone have anything to add before the meeting closes? (1 min)
- Closure (1 min)

Action Points:
- **TODO: Do the buddy check(!!!)**
- After the meeting we will create the issues and divide them.
