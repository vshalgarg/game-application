// import { useState } from "react";
// import { useNavigate } from "react-router-dom";

// const Signup = () => {

//   const navigate = useNavigate();

//   const [username, setUsername] = useState("");
//   const [email, setEmail] = useState("");
//   const [password, setPassword] = useState("");

//   const handleSignup = () => {

//     if (!username || !email || !password) {
//       alert("All fields are required");
//       return;
//     }

//     const users = JSON.parse(localStorage.getItem("users")) || [];
//     console.log(" user before signup", users)

//     // Check if user already exists
//     const userExists = users.find(
//       (u) => u.email === email || u.username === username
//     );

//     if (userExists) {
//       alert("User already exists");
//       return;
//     }

//     const newUser = { username, email, password };

//     users.push(newUser);

//     localStorage.setItem("users", JSON.stringify(users));

//     // Auto login after signup
//     //localStorage.setItem("user", JSON.stringify(newUser));
//     console.log(" user after signup", users)

//     navigate("/login");
//   };

//   return (

//     <div className="
//       min-h-screen
//       flex
//       items-center
//       justify-center
//       bg-gradient-to-br
//       from-black
//       via-gray-900
//       to-black
//       px-4
//     ">

//       <div className="
//         w-full
//         max-w-md
//         bg-white/10
//         backdrop-blur-lg
//         border
//         border-white/20
//         rounded-3xl
//         p-8
//         shadow-2xl
//       ">

//         <h1 className="text-4xl text-white font-bold text-center mb-6">
//           Create Account 🎮
//         </h1>

//         <p className="text-gray-300 text-center mb-8">
//           Join the game arena
//         </p>

//         {/* Username */}

//         <input
//           type="text"
//           placeholder="Username"
//           value={username}
//           onChange={(e) => setUsername(e.target.value)}
//           className="w-full mb-4 px-4 py-3 rounded-xl bg-black/30 text-white outline-none border border-purple-500/30 focus:border-purple-500"
//         />

//         {/* Email */}

//         <input
//           type="email"
//           placeholder="Email"
//           value={email}
//           onChange={(e) => setEmail(e.target.value)}
//           className="w-full mb-4 px-4 py-3 rounded-xl bg-black/30 text-white outline-none border border-blue-500/30 focus:border-blue-500"
//         />

//         {/* Password */}

//         <input
//           type="password"
//           placeholder="Password"
//           value={password}
//           onChange={(e) => setPassword(e.target.value)}
//           className="w-full mb-6 px-4 py-3 rounded-xl bg-black/30 text-white outline-none border border-green-500/30 focus:border-green-500"
//         />

//         {/* Button */}

//         <button
//           onClick={handleSignup}
//           className="
//             w-full
//             bg-green-500
//             hover:bg-green-600
//             py-3
//             rounded-xl
//             text-white
//             font-semibold
//             transition
//           "
//         >
//           Create Account
//         </button>

//         <p
//           onClick={() => navigate("/login")}
//           className="text-center text-gray-300 mt-6 cursor-pointer hover:text-white"
//         >
//           Already have an account? Login
//         </p>

//       </div>

//     </div>
//   );
// };

// export default Signup;


import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { signupUser } from "../services/authService";

const Signup = () => {
  const navigate = useNavigate();

  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");

  const handleSignup = async () => {
    console.log("Signup Button Clicked");

    if (!email || !password) {
      alert("All fields are required");
      return;
    }

    console.log("Email:", email);
    console.log("Password:", password);

    try {
      const response = await signupUser({
        email,
        password,
      });

      console.log("Signup Success:", response);

      alert("Account created successfully");

      navigate("/login");
    } catch (error) {
      console.log("Status:", error.response?.status);
      console.log("Data:", error.response?.data);
      console.log("Full Error:", error);

      alert(
        error.response?.data?.message ||
          "Failed to create account"
      );
    }
  };

  return (
    <div
      className="
        min-h-screen
        flex
        items-center
        justify-center
        bg-gradient-to-br
        from-black
        via-gray-900
        to-black
        px-4
      "
    >
      <div
        className="
          w-full
          max-w-md
          bg-white/10
          backdrop-blur-lg
          border
          border-white/20
          rounded-3xl
          p-8
          shadow-2xl
        "
      >
        <h1 className="text-4xl text-white font-bold text-center mb-6">
          Create Account 🎮
        </h1>

        <p className="text-gray-300 text-center mb-8">
          Join the game arena
        </p>

        <input
          type="email"
          placeholder="Email"
          value={email}
          onChange={(e) => setEmail(e.target.value)}
          className="w-full mb-4 px-4 py-3 rounded-xl bg-black/30 text-white outline-none border border-purple-500/30 focus:border-purple-500"
        />

        <input
          type="password"
          placeholder="Password"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
          className="w-full mb-6 px-4 py-3 rounded-xl bg-black/30 text-white outline-none border border-green-500/30 focus:border-green-500"
        />

        <button
          onClick={handleSignup}
          className="
            w-full
            bg-green-500
            hover:bg-green-600
            py-3
            rounded-xl
            text-white
            font-semibold
            transition
          "
        >
          Create Account
        </button>

        <p
          onClick={() => navigate("/login")}
          className="text-center text-gray-300 mt-6 cursor-pointer hover:text-white"
        >
          Already have an account? Login
        </p>
      </div>
    </div>
  );
};

export default Signup;