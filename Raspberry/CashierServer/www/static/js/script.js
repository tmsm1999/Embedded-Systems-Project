// Shortcuts to DOM Elements.
var sideBarList = document.getElementById('tmMainList');

var signInButton = document.getElementById('sign-in-button');
var signOutButton = document.getElementById('sign-out-button');

var archiveCashierList = document.getElementById('archive-CashierList');
var productsSection = document.getElementById('products');

var listeningFirebaseRefs = [];

// Get the Database service for the default app
var cashierDatabase = firebase.database();


/**
 * The ID of the currently signed-in User. We keep track of this to detect Auth state change events that are just
 * programmatic token refresh but not a User status change.
 */
 var currentUID;

 /**
  * Triggers every time there is a change in the Firebase auth state (i.e. user signed-in or user signed out).
  */
function onAuthStateChanged(user) {
  // We ignore token refresh events.
  if (user && currentUID === user.uid) {
    setupAuth(); 
    return;
  }

  cleanup();
  if (user) {
    currentUID = user.uid;
    //splashPage.style.display = 'none';
    writeUserData(user.uid, user.displayName, user.email);
    startDatabaseQueries();
    
  } else {
    // Set currentUID to null.
    currentUID = null;
  }
  setupAuth(); 
}

/**
 * Cleanups the UI and removes all Firebase listeners.
 */
 function cleanup() {
  // Stop all currently listening Firebase listeners.
  listeningFirebaseRefs.forEach(function(ref) {
    ref.off();
  });
  listeningFirebaseRefs = [];
}

/**
* Starts listening for changes on the cashiers.
*/
function startDatabaseQueries() {
  getCashierList();
}

/**
* Creates a new Cashier entry.
*/
function createCashierNavEntry(cashierID) {
  if(sideBarList.getElementsByClassName('tm-section-cashier-'+cashierID).length == 0){
    var li = document.createElement('li');  
    var html =
        '<a href="#cashier'+cashierID+'" id="tmNavCashier'+cashierID+'" class="scrolly tm-section-cashier-'+cashierID+'" data-bg-img="fundo4.jpeg" data-page="#tm-section-cashier-'+cashierID+'" data-page-type="carousel">'+
            '<i class="fas fa-shopping-cart tm-nav-fa-icon"></i>'+
            '<span>Cashier '+cashierID+'</span>'+
        '</a>';
    li.innerHTML=html;
    sideBarList.append(li);
  };
  setupNav();
}

 /**
  * Creates a product element.
  */
function createProductElements(cashierID,product) {
      // Listen for the products.
      var productsRef = cashierDatabase.ref('Cashiers/' + cashierID);
      productsRef.on('value', function(productSnapshot){
        productSnapshot.forEach(function(childSnapshot){
          if (childSnapshot.val() != "null") {
            if(document.getElementById("products"+cashierID).getElementsByClassName("product-"+childSnapshot.key).length == 0){
              var html =
              '<div class="product product-' + childSnapshot.key + ' mdl-cell mdl-cell--12-col mdl-cell--6-col-tablet mdl-cell--4-col-desktop mdl-grid mdl-grid--no-spacing">' +
                '<div class="mdl-card mdl-shadow--2dp">' +
                  '<div class="mdl-card__title mdl-color--light-blue-600 mdl-color-text--white">' +
                    '<h4 class="mdl-card__title-text">'+childSnapshot.key+'</h4>' +
                  '<div id="cash'+cashierID+'-'+childSnapshot.key+'" class="text">'+childSnapshot.val()+' Kg</div>' +
                  '</div>' +
                '</div>' +
              '</div>';

              var div = document.createElement('div');
              
              div.innerHTML = html;

              document.getElementById("products"+cashierID).append(div);
            } 
          }
        });
      });
    
      
      // Keep track of all Firebase reference on which we are listening.
      listeningFirebaseRefs.push(productsRef);

      //archive.onclick = onArchiveClicked;
}

 
/**
* Creates a Cashier element.
*/
  function createCashierElement(cashierID) {
    if(sideBarList.getElementsByClassName('scrolly').namedItem("tmNavCashier"+cashierID) != null && document.getElementById('tm-section-cashier-'+cashierID) == null){
      var html =

            '<!-- Cashier '+cashierID+' -->'+
                '<div class="ml-auto">'+
                    '<header class="mb-4"><h1 class="tm-text-shadow">Cashier '+cashierID+'</h1></header>'+
                      '<div id="products'+cashierID+'" class="material material-icons">'+
                        '<h2>Products</h2>'+
                      '</div>'+
                '</div>';

          

      // Create the DOM element from the HTML.

      var section = document.createElement('section');

      section.innerHTML = html;
      section.setAttribute("id", "tm-section-cashier-" + cashierID);
      section.setAttribute("class", "tm-section");
      section.setAttribute("style", "display: none;"); 
      
      //Append new cashier info
      document.getElementById('sectionContent').appendChild(section);

      cashierDatabase.ref('Cashiers/'+cashierID).once('value', (snapshot) => {
          createProductElements(cashierID,snapshot.key)
      });
    };
  }
  

 /**
  * Gets a list of products of a Cashier from the Firebase DB.
  */
  function getCashierList() {
    var cashierRef = cashierDatabase.ref('Cashiers');
    cashierRef.once('value', (snapshot) => {
      snapshot.forEach((childSnapshot) => {
        var childKey = childSnapshot.key;
        if (sideBarList.getElementsByClassName('scrolly').namedItem('tmNavCashier'+childKey) == null ){
          createCashierNavEntry(childKey);
          createCashierElement(childKey); 
        }
      });

      // Keep track of all Firebase reference on which we are listening.
      listeningFirebaseRefs.push(cashierRef);
    });
  };

  // Bindings on load.
window.addEventListener('load', function() {
  
  // Bind Sign in button.
  signInButton.addEventListener('click', function() {
    console.log("trying login");

    var provider = new firebase.auth.GoogleAuthProvider();
    firebase.auth().signInWithPopup(provider);
  });

  // Bind Sign out button.
  signOutButton.addEventListener('click', function() {
    firebase.auth().signOut();
    window.location.reload();
  });

  // Listen for auth state changes
  firebase.auth().onAuthStateChanged(onAuthStateChanged);

  // Bind menu buttons.
  tmSideBar.onclick = function() {
    if (firebase.auth().user!=null){
      startDatabaseQueries()
    }
  };

  tmSideBar.onclick();
}, false);


/**
 * Writes the user's data to the database.
 */
 function writeUserData(userId, name, email) {
  firebase.database().ref('users/' + userId).set({
    username: name,
    email: email,
  });
}
