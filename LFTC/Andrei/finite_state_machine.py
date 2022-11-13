class FiniteStateMachine:
    def __init__(self, lines):
        self.lines = lines

        self.alphabet = [*self.lines[0]]
        self.initial_states = self.lines[1].split(" ")
        self.final_states = self.lines[2].split(" ")
        self.states = self.lines[3].split(" ")
        self.transitions = [transition.split(" ") for transition in self.lines[4:]]

        self.adjacency = {}
        self.create_adjacency()

    def isNedeterminist(self):
        for i in range(len(self.transitions)):
            for j in range(len(self.transitions)):
                if i != j and \
                        self.transitions[i][0] == self.transitions[j][0] and \
                        self.transitions[i][1] == self.transitions[j][1]:
                    return True

        return False

    def create_adjacency(self):
        for state in self.states:
            self.adjacency[state] = {}
            for state2 in self.states:
                self.adjacency[state][state2] = None

        for transition in self.transitions:
            self.adjacency[transition[0]][transition[2]] = transition[1]

    def verify_element(self, element):
        current_state = self.initial_states[0]

        if current_state in self.final_states and element == "E":
            return True

        for char in element:
            found = False
            for state in self.states:
                if self.adjacency[current_state][state] != None and \
                        char in self.adjacency[current_state][state]:
                    found = True
                    current_state = state
                    break

            if not found:
                return False

        if current_state not in self.final_states:
            return False

        return True

    def longest_prefix_for_element(self, element):
        index = len(element)

        while index > 0:
            if self.verify_element(element[:index]):
                return element[:index]

            index -= 1

        if self.initial_states[0] in self.final_states:
            return "E"

        return None
