import json

def get_label(reseau_semantique, node_id):
    label = [node["label"] for node in reseau_semantique["nodes"] if node["id"] == node_id]
    return " ,".join(label)

def heritage(reseau_semantique, name):
    fin = False

    nodes = reseau_semantique["nodes"]
    edges = reseau_semantique["edges"]
   

    #get node where given name
    node = [node for node in nodes if node["label"] == name][0]

    #get all inherited nodes IDs
    direct_edges = [edge["to"] for edge in edges if (edge["from"] == node["id"] and edge["label"] == "is a")]

    all_edges = []
    properties = []
    while not fin:
        n = direct_edges.pop()
        #get inherited nodes label
        all_edges.append(get_label(reseau_semantique, n))
        #if the inherited are also inherited by other nodes
        direct_edges.extend([edge["to"] for edge in edges if (edge["from"] == n and edge["label"] == "is a")])
        
        #for inference
        properties_nodes = [edge for edge in edges if (edge["from"] == n and edge["label"] != "is a")]
        
        for pn in properties_nodes:
            properties.append(": ".join([pn["label"], get_label(reseau_semantique, pn["to"])]))
        if len(direct_edges) == 0:
            fin = True

    return all_edges, properties