# CTL
Data binding framework for Android applications

## Motivation
Android UI framework has a fairly complicated lifecycle. Most of UI code lays in system callbacks methods such as ```onCreate()``` or ```onResume()```. There are a _lot of_ boilerplate there. *A lot*.
 Consider you need a Fragment with some form EditText's, a button launches an async server query and a progress bar.
 You will need:
- Add a class fiend for every view you will need to change state (progress bar and all of form fields in our case);
- Overload an ```onCreate()``` and do ```setRetainInstance(true)```;
- Overload an ```onCreateView()```, inflate your .xml layout and find and cast all of views you need (button, progress bar and form fields). Don't forget to restore last instance state;
- Overload an ```onDestroyView()``` and set to null all of your views fields there;
- Implement an ```View.OnClickListener``` for a button you have and add some code to launch an async task. Don't forget to save async task somewhere in field to restore views state after rotation correctly;
- Add some code to ```onSaveInstanceState()``` to pass state to next instance of your fragment;
- In you async task completion handler don't forget to check fragment state before using it. It may be already destroyed or be somewhere in the middle of rotation process. If you got an exception, don't forget to invent some shitty way to store it until better times when you actually will be able to display a error message.

Whoa! Its a lot of work here! But what if I say, you don't need most of it?

What if you can just say: "Let my progress bar visibility will be bound with that async task state" and don't bother your Fragment state or even progress bar view existence?

What if you can deal with all you fragment logic using one method and less than 100 lines of code?

What if you can place all of you view- (or even business-) rules in form of actual rules? And leave all observing/lifecycle shit under the hood?

#### With CTL you can. Will be. Eventually.

## Disclaimer
CTL is under *heavy development*. Very heavy. Also it being developed on *Kotlin*. Mostly because client code will be use a lot of lambdas.
Java 8 currently not supported for Android applications and [retrolambda project](https://github.com/orfjackal/retrolambda) does not inspire any confidence so Kotlin is the only choice.

## Understanding
CTL stands on three main entities, which form its name:
- Cell
- Trigger
- Link

### Cell
Represents an observable piece of data. Cell can be a standalone, or wrap another state of a program.
For example, single ```Cell<String>``` can wrap around a text inside a ```TextView``` instance.

### Link
Using links, developer defines relations between Cells. Basically, links define a program data flow.
Every Link bounded with one dedicated Cell and Trigger. Link work is to take the data from source an put it to dedicated Cell when Trigger is armed.
Some types of Links may just sink other Cells state to dedicated one, other Links can get their data from remote server, and so on.
Most popular Link - RuleLink take values from a bunch of other Cells, process it with user-defined function and sinks it to dedicated Cell.

### Trigger
Trigger represents an observable event source in program. When something happens Trigger should became armed. Triggers are observed by other pats of framework.
When event is processed trigger should became disarmed.

## Boring and unreadable documentation
Coming soon.