# Asteroids Game

**Contributors:** [Ray Zhang](https://github.com/rayzhangdev-coder) and [Gitan Mandell](https://github.com/GitanElyon) | **Note:** This project was done in October 2025, this is a late post.

A feature-rich recreation of the classic arcade shooter built in Java with customized with acceleration physics, collision detection, and randomized asteroid spawning.<br>
Developed from scratch using OOP principles of inheritance & composition. UML Diagram attached below.
![Game Preview](https://github.com/rayzhangdev-coder/Asteroids-Game/blob/main/AsteroidsImage.png?raw=true)
![UML Diagram](https://github.com/rayzhangdev-coder/Asteroids-Game/blob/main/UML-Diagram-Asteroids.png?raw=true)

### Game Logic & Mechanics

The game operates on a progressive wave system where survival requires both precision shooting and strategic movement.

#### **Controls**

| Action | Key(s) |
| --- | --- |
| **Accelerate** | `↑` or `W` |
| **Rotate Ship** | `←` `→` or `A` `D` |
| **Shoot** | `SPACE` |
| **Restart Game** | `R` (on Game Over screen) |

#### **Difficulty Scaling**

The game gets progressively harder as the asteroid count and size variety increase.
* **Asteroid Scaling:** As you advance, the number of asteroids increases based on the wave number ($5 + \text{wave} \times 2$). The size of spawning asteroids varies by wave: Waves 1–3 feature only large asteroids, Waves 4–5 introduce medium sizes, and Wave 6 and beyond feature a full mix of small, medium, and large targets.
* **Shield Power-Up:** You begin the game with no protection. After successfully completing **Wave 5**, a special cutscene occurs where you are granted a **Shield**. that becomes active starting at Wave 6. The shield allows the ship to survive exactly one collision with an asteroid; the shield is destroyed upon impact, but the ship remains safe to continue.

---

### Custom Music

The game features a built-in music player that adds an atmospheric layer to the gameplay.

* **File Requirement:** The system looks for a file named `Song.wav`.
* **Installation:** To use your own music, convert any track to a `.wav` format and place it at the **same level** as the `Asteroids` project folder.
* **Looping:** The game will automatically loop this track continuously during gameplay.

---

### Setting up with Eclipse

Since this project includes `.project` and `.classpath` files, it is optimized for the Eclipse IDE.

1. **Clone or Download** the repository to your local machine.
2. Open **Eclipse IDE**.
3. Go to `File` > `Import...`
4. Select `General` > `Existing Projects into Workspace` and click **Next**.
5. Browse to the root folder where you saved the project.
6. Ensure the "Asteroids" project is checked and click **Finish**.
7. Run the game by right-clicking `Asteroids.java` in the `src/game` package and selecting `Run As` > `Java Application`.
