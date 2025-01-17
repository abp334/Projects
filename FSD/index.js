document.addEventListener("DOMContentLoaded", () => {
  // Function to animate numbers
  function loopNumber(id, maxValue, delay) {
    const element = document.getElementById(id);
    let count = 0;
    if (element) {
      const interval = setInterval(() => {
        if (count <= maxValue) {
          element.innerHTML = `${count}`;
          count++;
        } else {
          clearInterval(interval);
        }
      }, delay);
    }
  }

  loopNumber("numberloop1", 10, 70);
  loopNumber("numberloop2", 100, 10);
  loopNumber("numberloop3", 20, 50);
});
