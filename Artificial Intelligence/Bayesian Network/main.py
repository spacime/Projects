import sys
import copy
import itertools


def enumeration_ask(X, e, bn, vars):
    QX = {}
    for xi in [False, True]:
        e[X] = xi
        QX[xi] = enumeration_all(vars, e, bn)
        del e[X]
    return normalize(QX)


def enumeration_all(vars, e, bn):
    if len(vars) == 0:
        return 1.0
    Y = vars.pop()
    if Y in e:
        val = Pr(Y, e[Y], e, bn) * enumeration_all(vars, e, bn)
        vars.append(Y)
        return val
    else:
        total = 0
        e[Y] = True
        total += Pr(Y, True, e, bn) * enumeration_all(vars, e, bn)
        e[Y] = False
        total += Pr(Y, False, e, bn) * enumeration_all(vars, e, bn)
        del e[Y]
        vars.append(Y)
        return total


def Pr(var, val, e, bn):
    parents = bn[var][0]
    if len(parents) == 0:
        truePr = bn[var][1][None]
    else:
        parentVals = [e[parent] for parent in parents]
        truePr = bn[var][1][tuple(parentVals)]
    if val == True:
        return truePr
    else:
        return 1.0 - truePr


def normalize(QX):
    total = 0.0
    for val in QX.values():
        total += val
    for key in QX.keys():
        QX[key] /= total
    return QX


def cal_prob(query, bn, vars, decisions):
    index = 0
    X = {}
    Y = {}
    for k, v in query[1].iteritems():
        if index == 0:
            X[k] = v
        else:
            Y[k] = v
        index += 1
    Z = copy.deepcopy(Y)
    Z.update(query[2])

    # set the probability of decision
    all_variables = copy.deepcopy(X)
    all_variables.update(Z)
    for k, v in all_variables.iteritems():
        if k in decisions:
            if v == True:
                bn[k] = [[], {None: 1.0}]
            if v == False:
                bn[k] = [[], {None: 0.0}]

    prob1 = enumeration_ask(X.keys()[0], Z, bn, vars).get(X.values()[0])

    prob2 = 1.0
    parent = copy.deepcopy(query[2])

    for k, v in Y.iteritems():
        prob2 *= enumeration_ask(k, parent, bn, vars).get(v)
        parent[k] = v

    return prob1 * prob2


def cal_utility(query, bn, vars, decisions):
    query[0] = 'P'
    X = query[1]
    Y = query[2]
    ZZ = copy.deepcopy(X)
    ZZ.update(Y)
    utility = 0

    for k, v in bn['utility'][1].iteritems():
        flag = False
        prob_temp = 0
        children = {}
        for index, u_item in enumerate(bn['utility'][0]):
            children[u_item] = k[index]
            if u_item in ZZ.keys():
                if k[index] == ZZ[u_item]:
                    del children[u_item]
                else:
                    flag = True

        # whether has confliction between children and parent
        if flag:
            prob_temp = 0
        else:
            query_temp = ['P', children, ZZ]
            prob_temp = cal_prob(query_temp, bn, vars, decisions)

        utility += prob_temp * v

    return utility


if __name__ == "__main__":
    infilename = sys.argv[2]
    infile = open(infilename, 'r')
    # infile = open("./samples/sample04.txt", 'r')
    outfile = open("./output.txt", 'w')

    bn = {}
    q = []
    decisions = []
    MEU_order = []
    output_arr = []

    # Read Query
    query = infile.readline().strip()
    while query[0] is not '*':
        l_index = query.find('(')
        r_index = query.find(')')
        AB = query[l_index + 1: r_index].split('|')

        X = AB[0].split(",")
        X_dic = {}
        X_arr = []
        for x in X:
            X_dic[x.split("=")[0].strip()] = '+' in x
            if query[0] == 'M':
                X_arr.append(x.strip())

        if query[0] == 'M':
            MEU_order.append(X_arr)

        given_dic = {}
        if len(AB) > 1:
            given = AB[1].split(",")
            for g in given:
                given_dic[g.split('=')[0].strip()] = '+' in g

        q.append([query[0], X_dic, given_dic])

        query = infile.readline().strip()

    # Read Bayes Network
    lines = infile.readlines()
    enum = enumerate(lines)
    i = 0
    while i < len(lines):
        X = ''
        given = []
        prob_table = {}

        AB = enum.next()[1].strip().split('|')
        i += 1
        X = AB[0].strip()

        if len(AB) > 1:
            given = AB[1].strip().split(' ')
            for j in range(pow(2, len(given))):
                p = enum.next()[1].strip()
                table_row = p.split(' ')
                prob = float(table_row[0])
                prob_situation = []
                for c in table_row[1:]:
                    if c == '+':
                        prob_situation.append(True)
                    else:
                        prob_situation.append(False)

                prob_situation = tuple(prob_situation)
                prob_table[prob_situation] = prob

                i += 1
            if i < len(lines):
                enum.next()
                i += 1

        else:
            p = enum.next()[1].strip()
            if p != 'decision':
                prob_table[None] = float(p)
            else:
                prob_table[None] = 1.0
                decisions.append(X)

            if i < len(lines):
                enum.next()
                i += 1
            i += 1

        bn[X] = [given, prob_table]

    # End of read Bayes network

    # Build graph based on Bayes network
    bn_graph = {}
    for k in bn.keys():
        bn_graph[k] = [[], []]

    for k, v in bn.iteritems():
        for i in v[0]:
            bn_graph[i][1].append(k)

        bn_graph[k][0] = bn_graph[k][0] + v[0]
    # End of Build graph

    # Create ordered variables
    vars = []
    while len(bn_graph) > 0:
        marked = []
        for k, v in bn_graph.iteritems():
            if len(v[0]) == 0:
                marked.append(k)
                for c in v[1]:
                    bn_graph[c][0].remove(k)

        for m in marked:
            del bn_graph[m]
            vars.append(m)
    vars.reverse()
    # End of creating ordered variables

    # Answer the queries
    for query in q:
        if query[0] == 'P':
            output_arr.append("{0:.2f}".format(round(cal_prob(query, bn, vars, decisions), 2)))
            # print "{0:.2f}".format(round(cal_prob(query, bn, vars, decisions), 2))

        if query[0] == 'E':
            output_arr.append(str(int(round(cal_utility(query, bn, vars, decisions), 0))))
            # print int(round(cal_utility(query, bn, vars, decisions), 0))

        if query[0] == 'M':
            permutations = []
            permutations_list = list(itertools.product([True, False], repeat=len(decisions)))
            # for com in permutations_list:
            for combination_boolean in permutations_list:
                parenthesis = {}
                for index, d in enumerate(decisions):
                    parenthesis[d] = combination_boolean[index]

                permutations.append(parenthesis)

            MU = 0
            s_comb = ''
            for p in permutations:

                for k, v in p.iteritems():
                    if k in query[1].keys():
                        query[1][k] = v

                s = ''
                for d in MEU_order[0]:
                    if p[d]:
                        s += '+'
                    else:
                        s += '-'

                this_comb_utility = cal_utility(query, bn, vars, decisions)
                if this_comb_utility > MU:
                    s_comb = s
                    MU = this_comb_utility

            # print ' '.join(map(str, list(s_comb))), int(round(MU, 0))
            output_arr.append(' '.join(map(str, list(s_comb))) + " " + str(int(round(MU, 0))))

    outfile.write('\n'.join(map(str, output_arr)))
