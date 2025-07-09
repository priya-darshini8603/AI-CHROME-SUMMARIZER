// content.js

document.addEventListener("mouseup", function () {
    let selectedText = window.getSelection().toString().trim();
    if (selectedText.length > 0) {
        // Send the selected text to the extension window (popup.js)
        chrome.runtime.sendMessage({ action: "sendText", text: selectedText }, function(response) {
            if (chrome.runtime.lastError) {
                console.error("Error: ", chrome.runtime.lastError);
            } else {
                console.log('Response from popup:', response); // Log the response from the popup
            }
        });
    }
});
