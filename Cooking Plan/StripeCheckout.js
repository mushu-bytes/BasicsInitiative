var stripe = Stripe('pk_test_51HQynJGNtnHiw0AlWo2s4QLMKmEyuxcEP8ab6HQlWqQd8enJgr8IXmnHLhSy6IyWZUunxh7Tv3pU28tLLc0Wifvz00jKEEa1XX');
var elements = stripe.elements();


// Set up Stripe.js and Elements to use in checkout form
var elements = stripe.elements();
var style = {
  base: {
    color: "#32325d",
  }
};

var card = elements.create("card", { style: style });
card.mount("#card-element");