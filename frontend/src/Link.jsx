import {EVENTS} from "./consts.js";
export async function navigate(href) {
    window.history.pushState({},"",href);
    window.dispatchEvent(new Event(EVENTS.PUSHSTATE));
}
