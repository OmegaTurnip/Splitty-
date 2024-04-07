| Key          | Value                                                            |
|--------------|------------------------------------------------------------------|
| Date:        | 02/04/24                                                         |
| Time:        | 14.45-15.30                                                      |
| Location:    | Flux Hall B                                                      |
| Chair        | Billy                                                           |
| Minute Taker | Esther                                                        |
| Attendees:   | Paras Khan, Esther Wit, Billy Runne, Maurits Sloof |

Agenda Items:
-
Formalities
-
- Opening by chair (1 min)
- Check -in: How is everyone doing? (1 min)
- Announcements by the team (1 min)
    - Lasse is sick so we are switching the roles around
- Approval of the agenda - Does anyone have any additions? (1 min)
- Approval of last minutes - Did everyone read the minutes from the previous meeting? (2 min)
    - Have the tasks from last week been done? What have you worked on in the last week (1 min)
      - Paras: Admin-page dump functionality, websockets, language functionality clean up, keyboard functionality 
      - Maurtis: connecting API with exchange rate, DI exchange 
      - Esther: long-polling and alert from expense overview 
      - Billy: bug fix front-end and test-end, authentication admin page
  

TA-points
-
- Announcements by the TA (3 min)
  - code-freeze is next friday!
- Presentation of the current app to TA (5 min)
  - comment TA: !! document how to run your application !!
  - we showed admin-page, showed adding of event and usage of web-sockets
- Questions for TA (7 min)
    - Is there a possibility of still getting some HCI-Feedback if we ask questions about it?
      - TA can still take a look at it, now that we have fixed the server. 
    - What counts as a high contrast theme? 
      - As distinguishable as possible (eg black-white)
      - Nice to look up on the internet 
    -  I (Billy) could not get GEIT to work, as it says all our work is not committed, do you know what goes wrong?
      - Checking through Maurits is the solution
    - Connected with the API but used HTTP class in Java and Jackson-call, is that allowed? 
      - TA has asked this, waiting for response
      - additionally: Jackson for local-date-time is fine
    - Question by Paras: 
      - We need to have a configuration file
      - Do you have to be able to change the server while connected to client?
      - Spring profile file: able to run multiple ports 

Main talking Points: (Inform/ brainstorm/ decision-making/ discuss)
-
- Milestone for this week (10 min) 
  - Finish basic requirements 
    - addExpense should work 
    - server URLs configurable 
    - websocket & long-polling
  - TO DO after the meeting: make a list of what still needs to be done of the basic requirement
  - Testing
    - criterium is 80% -> next week
    - Testing should be in the merge request!! 25% of our grade
- Tasks and planning: (2 min)
    - Reminder to update issues and merge requests (tags, descriptions, time estimates)
    - Reminder to split up big merge requests and keep each mr to at most one issue
      - also really helps to not have 2000 lines of code 
    - Who uses issue boards?
      - Is really useful, check it out! TD all 
- How do we want to do the buttons in the application? Icon only or also text? (2 min)
  Both text and button for the controllers! TD week 9

 Merge request spend time: only write down your review time 
 
TD edit time estimate MR if you also interpreted this wrong 

TD check out presentation slides feedback

Rounding off
-
- Summarize action points: Who, what, when? (5 min)
- Feedback round: What went well and what can be improved next time? (2 min)
- Planned meeting duration != actual duration? Where/why did you mis -estimate? (2 min)
- Question round: Does anyone have anything to add before the meeting closes? (1 min)
  - TD Text Lasse
    - What still needs work? Do we need to take over something? 
- Closure (1 min)

Action Points 
- TD make a list of what we still need to do after the meeting 
- TO DO all (except Billy & Paras) check out issue boards 
- TO DO week 9: both text & button for the front-end
- TO DO all: edit MR time if you also interpeted this wrong 
- TD DO all: check out presentation slides feedback 
- TO DO Billy: text Lasse 

