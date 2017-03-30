package test.jta.xa;

import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
@Path("/items")
public class ItemRestController {

  private final ItemService itemService;

  @Context
  private UriInfo uriInfo;

  @Autowired
  public ItemRestController(ItemService itemService) {
    this.itemService = itemService;
  }

  @GET
  @Path("/{itemId}")
  @Produces(MediaType.APPLICATION_JSON)
  public Response getItem(@PathParam("itemId") Long itemId) {
    Item item =  itemService.getItem(itemId);
    return Response.ok(item).build();
  }

  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public Response create(Item item) {
    Item savedItem =  itemService.createItemAndNotify(item);
    UriBuilder builder = uriInfo.getAbsolutePathBuilder();
    builder.path(Long.toString(savedItem.getId()));  
    return Response.created(builder.build()).build();
  }

  @PUT
  @Path("/{itemId}")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public Response edit( @Valid Item item) {
    itemService.editItemAndNotify(item);
    return Response.ok().build();
  }

  @DELETE
  @Path("/{itemId}")
  public Response delete( @PathParam("itemId") Long itemId) {
    itemService.deleteItemAndNotify(itemId);
    return Response.ok().build();
  }

  @GET
  @Path("/count")
  public Response count( @PathParam("itemId") Long itemId) {
    return Response.ok(itemService.getItemCount()).build();
  }

}
