document.addEventListener('DOMContentLoaded', () => {
  const textInputArea = document.getElementById('selected-text');
  const summarizeButton = document.getElementById('summarize-btn');
  const suggestButton = document.getElementById('suggest-btn');
  const outputDisplay = document.getElementById('response');

  // Extract selected text from the active browser tab
  chrome.tabs.query({ active: true, currentWindow: true }, (tabs) => {
    chrome.scripting.executeScript(
      {
        target: { tabId: tabs[0].id },
        func: () => window.getSelection().toString(),
      },
      (results) => {
        if (results?.[0]?.result) {
          textInputArea.value = results[0].result;
        }
      }
    );
  });

  // Send request to backend with operation type
  const handleOperationRequest = async (operationType) => {
    const userInput = textInputArea.value.trim();

    if (!userInput) {
      outputDisplay.textContent = '⚠️ Please select or enter some text first.';
      return;
    }

    const requestData = {
      content: userInput,
      operation: operationType,
    };

    try {
      outputDisplay.textContent = '⏳ Processing your request...';

      const response = await fetch('http://localhost:8080/api/summary/process', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(requestData),
      });

      const responseText = await response.text();
      outputDisplay.textContent = responseText;
    } catch (error) {
      outputDisplay.textContent = '❌ Error: ' + error.message;
    }
  };

  //Event listeners for buttons
  summarizeButton.addEventListener('click', () => handleOperationRequest('summarize'));
  suggestButton.addEventListener('click', () => handleOperationRequest('suggest'));
});

