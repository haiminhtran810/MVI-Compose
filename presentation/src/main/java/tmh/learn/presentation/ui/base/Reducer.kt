package tmh.learn.presentation.ui.base

interface Reducer<State : Reducer.ViewState, Event : Reducer.ViewEvent, Effect : Reducer.ViewEffect> {
    /* This is a representation of the UI.
     In theory, this should contain everything the Compose screen needs to display.
     This has the added benefit of making it super easy to create multiple previews
     which each represent a different state of the screen. */
    interface ViewState;

    /* This is the core of the MVI as it holds all the user interactions (and a bit more).
     This is what will be used by the ViewModel to trigger state changes. */
    interface ViewEvent;

    /* This is a special kind of ViewEvent.
    Its role is to be fired into the UI by the ViewModel.
    Actions such as Navigation or displaying a Snackbar/Toast. */

    /* Note: It can also be triggered as a response to a ViewEvent
    (Updating something then navigating to a Success or Error screen based on the result).*/
    interface ViewEffect;


    // The reduce function takes a ViewState and a ViewEvent and produces a new ViewState and optionally a ViewEffect linked to the provided event.
    fun reduce(previousState: State, event: Event): Pair<State, Effect?>
}