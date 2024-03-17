import { GraphUIConfig } from "../config/GraphUIConfig";
import { FactoryGraphUI } from "../types/uiTypes";
import { ElementIdentifier } from "../utils/ElementIdentifier";
import { findStageInputPosition } from "../utils/utils";

export class InfoRenderer {
    private factoryGraph: FactoryGraphUI;
    private elementIdentifier: ElementIdentifier;

    constructor(private svg: d3.Selection<SVGSVGElement, unknown, HTMLElement, any>) {
        this.elementIdentifier = new ElementIdentifier();
    }


    public renderInfo(infoType: string, isVisible: boolean) {
        if (window.javaConnector) {
            window.javaConnector.log("Rendering info: " + infoType + " " + isVisible);
        } 
        switch (infoType) {
            case "capacities":
                this.renderCapacities(isVisible);
                break;
            case "priorities":
                this.renderPriorities(isVisible);
                break;
            case "quantities":
                this.renderQuantities(isVisible);
                break;
            default:
                break;
        }
    }
    
    renderCapacities(isVisible: boolean) {
        const { stageBoxWidth } = GraphUIConfig.node;
        const { infoFontSize, infoColor, infoPaddingX, capacityPaddingY } = GraphUIConfig.info;

        Object.entries(this.factoryGraph.nodes).forEach(([nodeId, nodeUI]) => {
            const textId = this.elementIdentifier.encodeCapacityTextId(nodeId);
            const textX = nodeUI.coordinates.x + stageBoxWidth / 2 + infoPaddingX;
            const textY = nodeUI.coordinates.y + capacityPaddingY;
            
            // Attempt selecting or otherwise create the text element
            let textElement = this.svg.select(`#${textId}`);
            if (textElement.empty()) {
                textElement = this.svg.append("text")
                    .attr("id", textId)
                    .attr("x", textX)
                    .attr("y", textY)
                    .style("font-size", infoFontSize)
                    .style("fill", infoColor);
            }
            
            // Set text and visibility
            textElement
                .text("C: " + nodeUI.node.numberOfStepsCapacity)
                .style("visibility", isVisible ? "visible" : "hidden");
        });
    }

    renderPriorities(isVisible: boolean) {
        const { stageBoxWidth } = GraphUIConfig.node;
        const { infoFontSize, infoColor, infoPaddingX, priorityPaddingY } = GraphUIConfig.info;

        Object.entries(this.factoryGraph.nodes).forEach(([nodeId, nodeUI]) => {
            const textId = this.elementIdentifier.encodePriorityTextId(nodeId);
            const textX = nodeUI.coordinates.x + stageBoxWidth / 2 + infoPaddingX;
            const textY = nodeUI.coordinates.y + priorityPaddingY;
            
            // Attempt selecting or otherwise create the text element
            let textElement = this.svg.select(`#${textId}`);
            if (textElement.empty()) {
                textElement = this.svg.append("text")
                    .attr("id", textId)
                    .attr("x", textX)
                    .attr("y", textY)
                    .style("font-size", infoFontSize)
                    .style("fill", infoColor);
            }
            
            // Set text and visibility
            textElement
                .text("P: " + nodeUI.node.priority)
                .style("visibility", isVisible ? "visible" : "hidden");
        });
    }

    renderQuantities(isVisible: boolean) {
        const { subnodeRadius } = GraphUIConfig.node;
        const { infoFontSize, infoColor, infoPaddingX, priorityPaddingY } = GraphUIConfig.info;

        Object.entries(this.factoryGraph.nodes).forEach(([nodeId, nodeUI]) => {
            const stageInputs = nodeUI.node.smallStage.stageInputs;
            stageInputs.forEach((input, index) => {
                const { x: stageInputX, y: stageInputY } = findStageInputPosition(nodeUI.coordinates.x, nodeUI.coordinates.y, stageInputs.length - 1, index);
                
                const textId = this.elementIdentifier.encodeQuantityTextId(nodeId, input.id);
                const textX = stageInputX + subnodeRadius + infoPaddingX;
                const textY = stageInputY;

                // Attempt selecting or otherwise create the text element
                let textElement = this.svg.select(`#${textId}`);
                if (textElement.empty()) {
                    textElement = this.svg.append("text")
                        .attr("id", textId)
                        .attr("x", textX)
                        .attr("y", textY)
                        .style("font-size", infoFontSize)
                        .style("fill", infoColor);
                }
                
                // Set text and visibility
                textElement
                    .text("Q: " + input.quantityPerStage)
                    .style("visibility", isVisible ? "visible" : "hidden");
            });
        });   
    }

    public setFactoryGraph(factoryGraph: FactoryGraphUI) {
        this.factoryGraph = factoryGraph;
    }
}